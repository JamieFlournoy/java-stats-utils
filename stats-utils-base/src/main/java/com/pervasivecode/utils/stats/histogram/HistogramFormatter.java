package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.Function;
import com.google.common.base.Strings;

/**
 * Format {@link Histogram} contents for a text display. The formatted output consists of a vertical
 * list of labeled histogram buckets, with horizontal bar graphs showing the count in each bucket,
 * followed by a percentage that represents the number of items counted in this bucket as a fraction
 * of all items counted by this histogram.
 * <p>
 * Example:
 * 
 * <pre>
 *     &lt;= 1 ******                                                         4%
 *     &lt;= 2 *********************                                         15%
 *     &lt;= 4 ************************************************************  43%
 *     &lt;= 8 ************************************************              34%
 *     &gt;  8 *****                                                          4%
 * </pre>
 *
 * @param <T> The type of value the histogram counted, which this formatter will format using the
 *        provided {@code upperBoundValueFormatter} parameter.
 * @see BucketingSystem
 */
public class HistogramFormatter<T> {
  private final HistogramFormat<T> format;

  /**
   * Create a ConsoleHistogramFormatter that formats upper bound values using
   * {@code upperBoundValueFormatter}.
   *
   * @param upperBoundValueFormatter A Function that converts an upper bound value into a String
   *        representation.
   * @param percentFormat A NumberFormat that formats the percentage value representing the
   *        part of the total count that is represented by the current bucket.
   * @param maxBarGraphWidth The maximum width of the bar graph portion of the formatted
   *        representation.
   */
  public HistogramFormatter(HistogramFormat<T> format) {
    this.format = Objects.requireNonNull(format);

    checkArgument(format.maxBarGraphWidth() > 1, "maxWidth must be at least 2.");
  }

  @Deprecated
  public HistogramFormatter(Function<T, String> upperBoundValueFormatter,
      NumberFormat percentFormat,
      int maxBarGraphWidth) {
    this(HistogramFormat.<T>builder() //
        .setUpperBoundValueFormatter(checkNotNull(upperBoundValueFormatter)) //
        .setPercentFormat(checkNotNull(percentFormat)) //
        .setMaxBarGraphWidth(maxBarGraphWidth) //
        .build());
  }

  public String format(Histogram<T> histogram) {
    checkArgument(histogram != null, "histogram is required.");
    final int bucketCount = histogram.numBuckets();
    checkState(bucketCount > 0);
   
    Function<T, String> upperBoundValueFormatter = format.upperBoundValueFormatter();
    int maxBarGraphWidth = format.maxBarGraphWidth();

    // Copy counts into an array, and find the largest count. This will be used to create a
    // horizontal bar graph made of asterisk characters.
    final long[] counts = new long[bucketCount];
    final String[] bucketLabels = new String[bucketCount];
    long totalCount = 0;
    long maxCount = 0;
    int maxBucketLabelLength = 0;
    final int lastBucket = counts.length - 1;
    for (int i = 0; i <= lastBucket; i++) {
      long count = histogram.countInBucket(i);
      counts[i] = count;
      if (maxCount < count) {
        maxCount = count;
      }
      totalCount += count;

      final String bucketLabel;
      if (i < lastBucket) {
        T bucketUpperBound = histogram.bucketUpperBound(i);
        bucketLabel = String.format("<= %s", upperBoundValueFormatter.apply(bucketUpperBound));
      } else {
        if (lastBucket > 0) {
          T openLowerBoundValue = histogram.bucketUpperBound(i - 1);
          bucketLabel = String.format(">  %s", upperBoundValueFormatter.apply(openLowerBoundValue));
        } else {
          bucketLabel = format.labelForSingularBucket();
        }
      }
      bucketLabels[i] = bucketLabel;

      if (maxBucketLabelLength < bucketLabel.length()) {
        maxBucketLabelLength = bucketLabel.length();
      }
    }

    // Do another pass (now that we know the total of counts) to determine the max width of the
    // percent column, so we can left-pad it correctly.
    int maxWidthOfPercent = 0;
    for (int i = 0; i <= lastBucket; i++) {
      final long bucketValue = counts[i];
      final double percent = (double) bucketValue / totalCount;
      final int widthOfFormattedPercent = format.percentFormat().format(percent).length();
      if (widthOfFormattedPercent > maxWidthOfPercent) {
        maxWidthOfPercent = widthOfFormattedPercent;
      }
    }
    final String paddedPercentFormat = "%" + maxWidthOfPercent + "s";
    
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i <= lastBucket; i++) {
      final long bucketValue = counts[i];
      final String bucketLabel = bucketLabels[i];
      final String paddedBucketLabel = Strings.padEnd(bucketLabel, maxBucketLabelLength, ' ');
      final double bucketValueAsFractionOfMax = (double) bucketValue / maxCount;
      final double bucketValueAsFractionOfTotal = (double) bucketValue / totalCount;
      final int numStars = new BigDecimal(bucketValueAsFractionOfMax * maxBarGraphWidth)
          .setScale(0, RoundingMode.HALF_EVEN).intValueExact();

      double percent = bucketValueAsFractionOfTotal;
      sb.append(paddedBucketLabel);
      sb.append(' ');
      sb.append(Strings.padEnd("", numStars, '*'));
      sb.append(Strings.padEnd("", maxBarGraphWidth - numStars, ' '));
      sb.append(' ');
      sb.append(String.format(paddedPercentFormat, format.percentFormat().format(percent)));
      sb.append('\n');
    }

    return sb.toString();
  }
}
