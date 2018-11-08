package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import javax.measure.Quantity;
import com.google.common.base.Strings;
import com.pervasivecode.utils.measure.api.QuantityFormatter;
import com.pervasivecode.utils.stats.histogram.Histogram;

/**
 * Render a vertical list of histogram buckets with horizontal bar graphs showing the count in
 * each bucket. 
 *
 * @param <Q> The type of value the histogram contained, and which this formatter will format.
 */
public class ConsoleHistogramQuantityFormatter<Q extends Quantity<Q>> {
  private final QuantityFormatter<Q> quantityFormatter;
  private final int maxWidth;

  public ConsoleHistogramQuantityFormatter(QuantityFormatter<Q> quantityFormatter) {
    this(quantityFormatter, 20);
  }

  public ConsoleHistogramQuantityFormatter(QuantityFormatter<Q> quantityFormatter, int maxWidth) {
    checkArgument(maxWidth > 1, "maxWidth must be at least 2.");
    this.quantityFormatter = checkNotNull(quantityFormatter);
    this.maxWidth = maxWidth;
  }

  public String format(Histogram<Quantity<Q>> histogram) {
    checkArgument(histogram != null, "histogram is required.");
    final int bucketCount = histogram.numBuckets();
    checkState(bucketCount > 0);

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
        Quantity<Q> bucketUpperBound = histogram.bucketUpperBound(i);
        bucketLabel = String.format("<= %s", quantityFormatter.format(bucketUpperBound));
      } else {
        if (lastBucket > 0) {
          Quantity<Q> openLowerBoundValue = histogram.bucketUpperBound(i - 1);
          bucketLabel = String.format(">  %s", quantityFormatter.format(openLowerBoundValue));
        } else {
          bucketLabel = "All";
        }
      }
      bucketLabels[i] = bucketLabel;

      if (maxBucketLabelLength < bucketLabel.length()) {
        maxBucketLabelLength = bucketLabel.length();
      }
    }

    StringBuilder sb = new StringBuilder();

    // TODO add optional empty-bucket trimming from output (no need to see smallest buckets w/ 0
    // count nor largest buckets w/ 0 count)
    for (int i = 0; i <= lastBucket; i++) {
      final long bucketValue = counts[i];
      final String bucketLabel = bucketLabels[i];
      final String paddedBucketLabel = Strings.padEnd(bucketLabel, maxBucketLabelLength, ' ');
      final double bucketValueAsFractionOfMax = (double) bucketValue / maxCount;
      final double bucketValueAsFractionOfTotal = (double) bucketValue / totalCount;
      final int numStars = (int) Math.round(bucketValueAsFractionOfMax * maxWidth);
      final int percent = (int) Math.round(bucketValueAsFractionOfTotal * 100);
      sb.append(paddedBucketLabel);
      sb.append(' ');
      sb.append(Strings.padEnd("", numStars, '*'));
      sb.append(Strings.padEnd("", maxWidth - numStars, ' '));
      sb.append(' ');
      sb.append(String.format("%3d%%", percent));
      sb.append('\n');
    }

    return sb.toString();
  }
}
