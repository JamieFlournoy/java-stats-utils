package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import java.text.NumberFormat;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;

public class HistogramFormatterTest {
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

  private HistogramFormatter<Double> histogramFormatterUs;
  private HistogramFormatter<Double> histogramFormatterGermany;
  private HistogramFormatter<Double> histogramFormatterDoubleAsHex;
  private NumberFormat germanyPercentFormat;

  @Before
  public void setup() {
    NumberFormat usNumberFormat = NumberFormat.getNumberInstance(Locale.US);
    usNumberFormat.setMinimumFractionDigits(1);
    histogramFormatterUs = new HistogramFormatter<Double>(//
        HistogramFormat.<Double>builder() //
            .setUpperBoundValueFormatter((d) -> usNumberFormat.format(d)) //
            .setLabelForSingularBucket("All")
            .setPercentFormat(NumberFormat.getPercentInstance(Locale.US)) //
            .setMaxBarGraphWidth(12) //
            .build());

    NumberFormat germanyNumberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
    germanyNumberFormat.setMinimumFractionDigits(1);
    germanyPercentFormat = NumberFormat.getPercentInstance(Locale.GERMANY);
    germanyPercentFormat.setMinimumFractionDigits(1);
    histogramFormatterGermany = new HistogramFormatter<Double>( //
        HistogramFormat.<Double>builder() //
            .setUpperBoundValueFormatter((d) -> germanyNumberFormat.format(d)) //
            .setLabelForSingularBucket("Alles")
            .setPercentFormat(germanyPercentFormat) //
            .setMaxBarGraphWidth(7) //
            .build());

    NumberFormat otherUsNumberFormat = NumberFormat.getPercentInstance(Locale.US);
    otherUsNumberFormat.setMaximumFractionDigits(3);
    histogramFormatterDoubleAsHex = new HistogramFormatter<Double>( //
        HistogramFormat.<Double>builder() //
            .setUpperBoundValueFormatter((d) -> Double.toHexString(d)) //
            .setLabelForSingularBucket("∀")
            .setPercentFormat(otherUsNumberFormat) //
            .setMaxBarGraphWidth(10) //
            .build());
  }

  private String dePct(double value) {
    return germanyPercentFormat.format(value);
  }
  
  @Test
  public void format_oneBucket_withUsFormat_shouldWork() {
    String expected = "All ************ 100%\n";
    assertThat(histogramFormatterUs.format(oneBucketHistogram())).isEqualTo(expected);
  }  

  @Test
  public void format_oneBucket_withGermanFormat_shouldWork() {
    String expected = "Alles ******* " + dePct(1.0) + "\n";
    assertThat(histogramFormatterGermany.format(oneBucketHistogram())).isEqualTo(expected);
  }

  @Test
  public void format_oneBucket_withHexFormat_shouldWork() {
    String expected = "∀ ********** 100%\n";
    assertThat(histogramFormatterDoubleAsHex.format(oneBucketHistogram())).isEqualTo(expected);
  }
  
  @Test
  public void format_threeBuckets_withUsFormat_shouldWork() {
    assertThat(histogramFormatterUs.format(threeBucketHistogram())).isEqualTo("" //
        + "<= 1.0 **           12%\n" //
        + "<= 5.0 *****        25%\n" //
        + ">  5.0 ************ 62%\n");
  }  

  @Test
  public void format_threeBuckets_withGermanFormat_shouldWork() {
    StringBuilder gsb = new StringBuilder();
    gsb.append("<= 1,0 *       ").append(dePct(0.125)).append("\n");
    gsb.append("<= 5,0 ***     ").append(dePct(0.250)).append("\n");
    gsb.append(">  5,0 ******* ").append(dePct(0.625)).append("\n");
    assertThat(histogramFormatterGermany.format(threeBucketHistogram())).isEqualTo(gsb.toString());
  }  
  @Test
  public void format_threeBuckets_withHexFormat_shouldWork() {
    assertThat(histogramFormatterDoubleAsHex.format(threeBucketHistogram())).isEqualTo("" //
        + "<= 0x1.0p0 **         12.5%\n" //
        + "<= 0x1.4p2 ****         25%\n" //
        + ">  0x1.4p2 ********** 62.5%\n");
  }
  
  @Test
  public void format_fourBuckets_withUsFormat_shouldWork() {
    assertThat(histogramFormatterUs.format(fourBucketHistogram())).isEqualTo("" //
        + "<= 1.0  *********    33%\n" //
        + "<= 5.0  ******       22%\n" //
        + "<= 25.0               0%\n" //
        + ">  25.0 ************ 44%\n");
  }

  @Test
  public void format_fourBuckets_withGermanFormat_shouldWork() {
    StringBuilder gsb = new StringBuilder();
    gsb.append("<= 1,0  *****   ").append(dePct(0.333)).append("\n");
    gsb.append("<= 5,0  ****    ").append(dePct(0.222)).append("\n");
    gsb.append("<= 25,0          ").append(dePct(0)).append("\n");
    gsb.append(">  25,0 ******* ").append(dePct(0.444)).append("\n");
    assertThat(histogramFormatterGermany.format(fourBucketHistogram())).isEqualTo(gsb.toString());
  }

  @Test
  public void format_fourBuckets_withHexFormat_shouldWork() {
    assertThat(histogramFormatterDoubleAsHex.format(fourBucketHistogram())).isEqualTo("" //
        + "<= 0x1.0p0 ********   33.333%\n" //
        + "<= 0x1.4p2 *****      22.222%\n" //
        + "<= 0x1.9p4                 0%\n" //
        + ">  0x1.9p4 ********** 44.444%\n");
  }
}
