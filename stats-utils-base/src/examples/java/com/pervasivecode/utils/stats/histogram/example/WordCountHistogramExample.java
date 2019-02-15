package com.pervasivecode.utils.stats.histogram.example;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.BucketSelectors;
import com.pervasivecode.utils.stats.histogram.ConcurrentHistogram;
import com.pervasivecode.utils.stats.histogram.HistogramFormat;
import com.pervasivecode.utils.stats.histogram.HistogramFormatter;
import com.pervasivecode.utils.stats.histogram.MutableHistogram;
import com.pervasivecode.utils.stats.histogram.example.ExampleApplication;

/**
 * Demonstration of how to create, populate, and format the contents of a {@link Histogram}
 * counting word lengths for each of the words in a text document.
 */
public class WordCountHistogramExample implements ExampleApplication {
  private static final String ULYSSES_RESOURCE_PATH =
      "com/pervasivecode/utils/stats/histogram/example/ulysses_by_alfred_tennyson.txt";

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
      int wordCount = wordCounts.get(i);
      numWords += wordCount;
      output.println(String.format("%d chars:\t%d", i + 1, wordCount));
    }
    output.print("Total number of words: ");
    output.println(numWords);
    output.println();

    NumberFormat usNumberFormat = NumberFormat.getInstance(Locale.US);
    HistogramFormatter<Long> histoFormatter = new HistogramFormatter<Long>( //
        HistogramFormat.<Long>builder() //
            .setUpperBoundValueFormatter((v) -> usNumberFormat.format(v)) //
            .setLabelForSingularBucket("All") //
            .setPercentFormat(NumberFormat.getPercentInstance(Locale.US)) //
            .setMaxWidth(69) //
            .build());

    output.println("As a histogram:");
    output.println(histoFormatter.format(histo));
  }

  public static void main(String[] args) throws Exception {
    OutputStreamWriter osw = new OutputStreamWriter(System.out, UTF_8);
    new WordCountHistogramExample().runExample(new PrintWriter(osw, true));
  }
}
