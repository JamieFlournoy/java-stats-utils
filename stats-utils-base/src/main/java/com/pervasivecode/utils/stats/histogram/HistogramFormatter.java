package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.math.RoundingMode.HALF_EVEN;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

/**
 * Format {@link Histogram} contents for a text display. The formatted output consists of a vertical
 * list of labeled histogram buckets, with horizontal bar graphs showing the count in each bucket,
 * followed by a percentage that represents the number of items counted in this bucket as a fraction
 * of all items counted by this histogram.
 * <p>
 * Example:
 *
 * <pre>
 *     &lt;= 1 ******                                                        4%
 *     &lt;= 2 *********************                                        15%
 *     &lt;= 4 ************************************************************ 43%
 *     &lt;= 8 ************************************************             34%
 *     &gt;  8 *****                                                         4%
 * </pre>
 *
 * @param <T> The type of value the histogram counted.
 * @see BucketingSystem
 */
public class HistogramFormatter<T> {
  private final HistogramFormat<T> format;

  /**
   * Create a HistogramFormatter that formats values according to the settings in the {@code format}
   * parameter.
   *
   * @param format A HistogramFormat that describes how the formatted histogram should look.
   */
  public HistogramFormatter(HistogramFormat<T> format) {
    this.format = Objects.requireNonNull(format);

    checkArgument(format.maxBarGraphWidth() > 1, "maxWidth must be at least 2.");
  }

  public String format(Histogram<T> histogram) {
    checkArgument(histogram != null, "histogram is required.");
    return formatInternal(ImmutableHistogram.from(histogram));
  }

  private String formatInternal(ImmutableHistogram<T> histogram) {
    final int bucketCount = histogram.numBuckets();
    checkState(bucketCount > 0);
    final int lastBucket = bucketCount - 1;

    final String[] bucketLabels = new String[bucketCount];
    final String[] formattedPercentages = new String[bucketCount];

    int maxBucketLabelLength = 0;
    int maxWidthOfPercent = 0;
    for (int i = 0; i <= lastBucket; i++) {
      final String bucketLabel = getBucketLabel(i, lastBucket, histogram);
      bucketLabels[i] = bucketLabel;
      if (maxBucketLabelLength < bucketLabel.length()) {
        maxBucketLabelLength = bucketLabel.length();
      }

      final double percent = (double) histogram.countInBucket(i) / histogram.totalCount();
      final String formattedPercentage = format.percentFormat().format(percent);
      formattedPercentages[i] = formattedPercentage;

      final int widthOfFormattedPercent = formattedPercentage.length();
      if (widthOfFormattedPercent > maxWidthOfPercent) {
        maxWidthOfPercent = widthOfFormattedPercent;
      }
    }

    final String labelFormat = "%-" + maxBucketLabelLength + "s";
    final String percentFormat = "%" + maxWidthOfPercent + "s";

    final long maxCount = histogram.maxCount();
    final int maxBarGraphWidth = format.maxBarGraphWidth();
    final double widthPerBucketUnit = ((double) maxBarGraphWidth) / maxCount;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i <= lastBucket; i++) {
      final double numStars = histogram.countInBucket(i) * widthPerBucketUnit;
      final int numWholeStars = new BigDecimal(numStars).setScale(0, HALF_EVEN).intValueExact();

      sb.append(String.format(labelFormat, bucketLabels[i]));
      sb.append(' ');
      for (int b = 0; b < maxBarGraphWidth; b++) {
        sb.append(b < numWholeStars ? '*' : ' ');
      }
      sb.append(' ');
      sb.append(String.format(percentFormat, formattedPercentages[i]));
      sb.append('\n');
    }
    return sb.toString();
  }

  private String getBucketLabel(int bucketIndex, int lastBucket, Histogram<T> histogram) {
    final Function<T, String> upperBoundValueFormatter = format.upperBoundValueFormatter();
    final int i = bucketIndex;
    if (i < lastBucket) {
      T bucketUpperBound = histogram.bucketUpperBound(i);
      return String.format("<= %s", upperBoundValueFormatter.apply(bucketUpperBound));
    } else {
      if (lastBucket > 0) {
        T openLowerBoundValue = histogram.bucketUpperBound(i - 1);
        return String.format(">  %s", upperBoundValueFormatter.apply(openLowerBoundValue));
      } else {
        return format.labelForSingularBucket();
      }
    }
  }
}
