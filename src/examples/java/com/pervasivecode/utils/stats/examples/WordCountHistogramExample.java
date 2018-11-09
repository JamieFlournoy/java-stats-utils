package com.pervasivecode.utils.stats.examples;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import com.pervasivecode.utils.measure.api.QuantityFormatter;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.BucketSelectors;
import com.pervasivecode.utils.stats.histogram.ConcurrentHistogram;
import com.pervasivecode.utils.stats.histogram.Histogram;
import com.pervasivecode.utils.stats.histogram.Histograms;
import com.pervasivecode.utils.stats.histogram.MutableHistogram;
import com.pervasivecode.utils.stats.histogram.measure.ConsoleHistogramQuantityFormatter;
import tec.uom.se.AbstractUnit;

public class WordCountHistogramExample implements ExampleApplication {
  private static final String ULYSSES_RESOURCE_PATH =
      "com/pervasivecode/utils/stats/examples/ulysses_by_alfred_tennyson.txt";

  @Override
  public void runExample(PrintWriter output) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource(ULYSSES_RESOURCE_PATH), UTF_8);

    // Bucket word-lengths into 5 buckets with upper bounds 2^0, 2^1, 2^2, 2^3, 2^4, and unbounded:
    // [Integer.MIN_VALUE..1], (1..2], (2..4], (4..8], (8..Integer.MAX_VALUE]
    BucketSelector<Long> bucketer = BucketSelectors.powerOf2LongValues(0, 5);
    MutableHistogram<Long> histo = new ConcurrentHistogram<>(bucketer);

    Predicate<String> isWord = Pattern.compile("\\w+").asPredicate();
    Splitter whitespaceSplitter = Splitter.on(Pattern.compile("\\s+"));

    // ConcurrentHistogram is thread-safe, so go ahead and tokenize->filter->count in parallel.
    lines.parallelStream() //
        .flatMap((line) -> whitespaceSplitter.splitToList(line).stream()) //
        .filter(isWord).forEach((w) -> histo.countValue((long) w.length()));

    // Count words in an array that doesn't do bucketing.
    AtomicIntegerArray wordCounts = new AtomicIntegerArray(13);
    lines.parallelStream() //
        .flatMap((line) -> whitespaceSplitter.splitToList(line).stream()) //
        .filter(isWord) //
        .forEach((w) -> wordCounts.incrementAndGet(w.length() - 1));
    output.println("Characters per word:");
    int numWords = 0;
    for (int i = 0; i < wordCounts.length(); i++) {
      numWords++;
      output.println(String.format("%d chars:\t%d", i + 1, wordCounts.get(i)));
    }
    output.print("Total number of words: ");
    output.print(numWords);
    output.println();

    // TODO make and then use a non-quantity version of ConsoleHistogramQuantityFormatter,
    // to avoid all this unnecessary setup for a unitless value.

    ServiceProvider measureServiceProvider = ServiceProvider.current();
    QuantityFactory<Dimensionless> qtyFactory =
        measureServiceProvider.getQuantityFactory(Dimensionless.class);
    Unit<Dimensionless> dimensionlessUnit = AbstractUnit.ONE;
    QuantityFormatter<Dimensionless> formatter = new QuantityFormatter<Dimensionless>() {
      @Override
      public String format(Quantity<Dimensionless> quantity) {
        return String.valueOf(quantity.getValue().longValue());
      }
    };

    ConsoleHistogramQuantityFormatter<Dimensionless> histoFormatter =
        new ConsoleHistogramQuantityFormatter<>(formatter, 60);
    Function<Long, Quantity<Dimensionless>> transformation =
        (val) -> qtyFactory.create(val, dimensionlessUnit);
    Histogram<Quantity<Dimensionless>> histoAsDimensionless =
        Histograms.transformValues(histo, transformation);

    output.println("As a histogram:");
    output.println(histoFormatter.format(histoAsDimensionless));
  }

  public static void main(String[] args) throws Exception {
    new WordCountHistogramExample().runExample(new PrintWriter(System.out, true, UTF_8));
  }
}
