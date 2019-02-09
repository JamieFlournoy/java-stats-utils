package com.pervasivecode.utils.stats.histogram.measure.example;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Power;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import com.google.common.io.Resources;
import com.pervasivecode.utils.measure.QuantityFormatter;
import com.pervasivecode.utils.measure.QuantityPrefixSelector;
import com.pervasivecode.utils.measure.ScalingFormatter;
import com.pervasivecode.utils.measure.SiPrefixSelector;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.ConcurrentHistogram;
import com.pervasivecode.utils.stats.histogram.Histogram;
import com.pervasivecode.utils.stats.histogram.measure.ConsoleHistogramQuantityFormatter;
import com.pervasivecode.utils.stats.histogram.measure.QuantityBucketSelectors;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.MetricPrefix;
import tec.uom.se.unit.Units;

/**
 * Demonstration of how to create, populate, and format the contents of a {@link Histogram}
 * counting {@link Quantity}{@code <}{@link Power}{@code >} values representing the electricity
 * generation capacities of over 28,000 known power plants worldwide.
 */
public class PowerPlantCapacityHistogramExample implements ExampleApplication {
  private static final String CAPACITIES_RESOURCE_PATH = "com/pervasivecode/utils/stats/"
      + "histogram/measure/example/global_power_plant_database-extract.csv";

  @Override
  public void runExample(PrintWriter output) throws IOException {
    URL csvFileUrl = Resources.getResource(CAPACITIES_RESOURCE_PATH);
    List<String> lines = Resources.readLines(csvFileUrl, UTF_8);
    lines.remove(0); // Drop the first line since it's a header row.

    // We can't get Watts from the javax.measure.spi.ServiceProvider API (si.uom.SI seems not to
    // work correctly in v0.9) so we'll just use the implementation class directly.
    Unit<Power> baseUnit = Units.WATT;
    Unit<Power> inputFileUnit = MetricPrefix.MEGA(Units.WATT);
    QuantityFactory<Power> quantityFactory =
        ServiceProvider.current().getQuantityFactory(Power.class);

    // Set up buckets for intervals (-infinity kW..1kW], (1kW..10kW) ... (10GW..infinity kW)
    BucketSelector<Quantity<Power>> bucketer =
        QuantityBucketSelectors.exponential(10, baseUnit, quantityFactory, 3, 9);

    // Make a histogram using that bucketer, which counts power quantities.
    ConcurrentHistogram<Quantity<Power>> histo = new ConcurrentHistogram<>(bucketer);

    for (String line : lines) {
      histo.countValue(Quantities.getQuantity(Double.parseDouble(line), inputFileUnit));
    }

    QuantityPrefixSelector prefixer = new SiPrefixSelector();
    NumberFormat numFormatter = NumberFormat.getInstance(Locale.US);
    QuantityFormatter<Power> formatter = new ScalingFormatter<>(baseUnit, prefixer, numFormatter);
    ConsoleHistogramQuantityFormatter<Power> histoFormatter =
        new ConsoleHistogramQuantityFormatter<>(formatter, 60);

    output.println("As a histogram:");
    output.println(histoFormatter.format(histo));
  }

  public static void main(String[] args) throws Exception {
    OutputStreamWriter osw = new OutputStreamWriter(System.out, UTF_8);
    new PowerPlantCapacityHistogramExample().runExample(new PrintWriter(osw, true));
  }
}
