package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import static com.pervasivecode.utils.stats.histogram.HistogramBucketCountFormatters.percentFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;

public class HistogramFormatterTest {
  // TODO Simplify this test. Move some of this into HorizontalBarGraphTest.

  private static ImmutableHistogram<Double> oneBucketHistogram() {
    return ImmutableHistogram.<Double>builder() //
        .setBucketUpperBounds(ImmutableList.of()) //
        .setCountByBucket(ImmutableList.of(1L)) //
        .build();
  }

  private static ImmutableHistogram<Double> threeBucketHistogram() {
    return ImmutableHistogram.<Double>builder() //
        .setBucketUpperBounds(ImmutableList.of(1.0, 5.0)) //
        .setCountByBucket(ImmutableList.of(1L, 2L, 5L)) //
        .build();
  }

  private static ImmutableHistogram<Double> fourBucketHistogram() {
    return ImmutableHistogram.<Double>builder() //
        .setBucketUpperBounds(ImmutableList.of(1.0, 5.0, 25.0)) //
        .setCountByBucket(ImmutableList.of(3L, 2L, 0L, 4L)) //
        .build();
  }

  private HistogramFormatter<Double> histogramFormatterUs(int width) {
    NumberFormat usNumberFormat = NumberFormat.getNumberInstance(Locale.US);
    usNumberFormat.setMinimumFractionDigits(1);
    return new HistogramFormatter<Double>(//
        HistogramFormat.<Double>builder() //
            .setUpperBoundValueFormatter((d) -> usNumberFormat.format(d)) //
            .setLabelForSingularBucket("All")
            .setBucketCountFormatter(percentFormatter(Locale.US)) //
            .setMaxWidth(width) //
            .build());
  }

  private HistogramFormatter<Double> histoFormatterGermany(int width) {
    NumberFormat germanyNumberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
    germanyNumberFormat.setMinimumFractionDigits(1);
    return new HistogramFormatter<Double>( //
        HistogramFormat.<Double>builder() //
            .setUpperBoundValueFormatter((d) -> germanyNumberFormat.format(d)) //
            .setLabelForSingularBucket("Alles")
            .setBucketCountFormatter(percentFormatter(germanyPercentFormat)) //
            .setMaxWidth(width) //
            .build());
  }

  private HistogramFormatter<Double> histogramFormatterDoubleAsHex(int width) {
    NumberFormat otherUsNumberFormat = NumberFormat.getPercentInstance(Locale.US);
    otherUsNumberFormat.setMaximumFractionDigits(3);
    return new HistogramFormatter<Double>( //
        HistogramFormat.<Double>builder() //
            .setUpperBoundValueFormatter((d) -> Double.toHexString(d)) //
            .setLabelForSingularBucket("∀")
            .setBucketCountFormatter(percentFormatter(otherUsNumberFormat)) //
            .setMaxWidth(width) //
            .build());
  }

  private NumberFormat germanyPercentFormat;

  @Before
  public void setup() {
    germanyPercentFormat = NumberFormat.getPercentInstance(Locale.GERMANY);
    germanyPercentFormat.setMinimumFractionDigits(1);
  }

  private String dePct(double value) {
    return germanyPercentFormat.format(value);
  }

  @Test
  public void format_withNullHistogram_shouldThrow() {
    try {
      histogramFormatterUs(20).format(null);
      Truth.assert_().fail("Expected an exception due to the null histogram parameter.");
    } catch (NullPointerException npe) {
      assertThat(npe).hasMessageThat().contains("histogram");
    }
  }

  @Test
  public void format_oneBucket_withUsFormat_shouldWork() {
    String expected = "All ************ 100%\n";
    assertThat(histogramFormatterUs(21).format(oneBucketHistogram())).isEqualTo(expected);
  }

  @Test
  public void format_oneBucket_withGermanFormat_shouldWork() {
    String expected = "Alles ******* " + dePct(1.0) + "\n";
    int width = expected.length() - 1;
    assertThat(histoFormatterGermany(width).format(oneBucketHistogram())).isEqualTo(expected);
  }

  @Test
  public void format_oneBucket_withHexFormat_shouldWork() {
    String expected = "∀ ********** 100%\n";
    assertThat(histogramFormatterDoubleAsHex(17).format(oneBucketHistogram())).isEqualTo(expected);
  }

  @Test
  public void format_threeBuckets_withUsFormat_shouldWork() {
    assertThat(histogramFormatterUs(23).format(threeBucketHistogram())).isEqualTo("" //
        + "<= 1.0 **           12%\n" //
        + "<= 5.0 *****        25%\n" //
        + ">  5.0 ************ 62%\n");
  }

  @Test
  public void format_threeBuckets_withGermanFormat_shouldWork() {
    String expectedLine1 = "<= 1,0 *       " + dePct(0.125) + "\n";
    String expectedLine2 = "<= 5,0 ***     " + dePct(0.250) + "\n";
    String expectedLine3 = ">  5,0 ******* "+ dePct(0.625) + "\n";

    int width = expectedLine1.length() - 1;
    String expected = expectedLine1 + expectedLine2 + expectedLine3;
    assertThat(histoFormatterGermany(width).format(threeBucketHistogram())).isEqualTo(expected);
  }
  @Test
  public void format_threeBuckets_withHexFormat_shouldWork() {
    assertThat(histogramFormatterDoubleAsHex(27).format(threeBucketHistogram())).isEqualTo("" //
        + "<= 0x1.0p0 **         12.5%\n" //
        + "<= 0x1.4p2 ****         25%\n" //
        + ">  0x1.4p2 ********** 62.5%\n");
  }

  @Test
  public void format_fourBuckets_withUsFormat_shouldWork() {
    assertThat(histogramFormatterUs(24).format(fourBucketHistogram())).isEqualTo("" //
        + "<= 1.0  *********    33%\n" //
        + "<= 5.0  ******       22%\n" //
        + "<= 25.0               0%\n" //
        + ">  25.0 ************ 44%\n");
  }

  @Test
  public void format_fourBuckets_withGermanFormat_shouldWork() {
    String expectedLine1 = "<= 1,0  *****   " + dePct(0.333) + "\n";
    String expectedLine2 = "<= 5,0  ****    " + dePct(0.222) + "\n";
    String expectedLine3 = "<= 25,0          " + dePct(0) + "\n";
    String expectedLine4 = ">  25,0 ******* " + dePct(0.444) + "\n";

    int width = expectedLine1.length() - 1;
    String expected = expectedLine1 + expectedLine2 + expectedLine3 + expectedLine4;

    assertThat(histoFormatterGermany(width).format(fourBucketHistogram())).isEqualTo(expected);
  }

  @Test
  public void format_fourBuckets_withHexFormat_shouldWork() {
    assertThat(histogramFormatterDoubleAsHex(29).format(fourBucketHistogram())).isEqualTo("" //
        + "<= 0x1.0p0 ********   33.333%\n" //
        + "<= 0x1.4p2 *****      22.222%\n" //
        + "<= 0x1.9p4                 0%\n" //
        + ">  0x1.9p4 ********** 44.444%\n");
  }
}
