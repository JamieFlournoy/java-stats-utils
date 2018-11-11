package com.pervasivecode.utils.stats.histogram.measure;

import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;
import javax.measure.Quantity;
import javax.measure.quantity.Time;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import com.pervasivecode.utils.measure.impl.ScalingDurationFormatter;
import com.pervasivecode.utils.stats.histogram.Histogram;
import com.pervasivecode.utils.stats.histogram.ImmutableHistogram;
import com.pervasivecode.utils.stats.histogram.measure.ConsoleHistogramQuantityFormatter;

public class ConsoleHistogramQuantityFormatterTest {
  private QuantityFactory<Time> quantityOfTimeFactory;
  private ConsoleHistogramQuantityFormatter<Time> formatter;


  @Before
  public void setUp() throws Exception {
    ServiceProvider measureServiceProvider = ServiceProvider.current();
     quantityOfTimeFactory = measureServiceProvider.getQuantityFactory(Time.class);
    formatter = new ConsoleHistogramQuantityFormatter<>(ScalingDurationFormatter.US(), 20);
  }

  private Quantity<Time> qtyOfMillis(long value) {
    return quantityOfTimeFactory.create(value, MILLI(SECOND));
  }

  @Test
  public void format_withOneValue() {
    ImmutableHistogram.Builder<Quantity<Time>> builder = ImmutableHistogram.builder();
    builder.setBucketUpperBounds(ImmutableList.of());
    builder.setCountByBucket(ImmutableList.of(37L));
    Histogram<Quantity<Time>> histogram = builder.build();

    Truth.assertThat(formatter.format(histogram)).isEqualTo("All ******************** 100%\n");
  }

  @Test
  public void format_withThreeValues() {
    StringBuilder sb = new StringBuilder();
    sb.append("<= 10ms  *********             25%\n");
    sb.append("<= 100ms *******               20%\n");
    sb.append(">  100ms ********************  55%\n");
    String expected = sb.toString();

    ImmutableHistogram.Builder<Quantity<Time>> builder = ImmutableHistogram.builder();
    builder.setBucketUpperBounds(ImmutableList.of(qtyOfMillis(10L), qtyOfMillis(100L)));
    builder.setCountByBucket(ImmutableList.of(25L, 20L, 55L));
    Histogram<Quantity<Time>> histogram = builder.build();

    Truth.assertThat(formatter.format(histogram)).isEqualTo(expected);
  }
}
