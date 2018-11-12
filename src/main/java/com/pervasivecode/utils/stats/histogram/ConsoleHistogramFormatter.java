package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.util.function.Function;
import com.google.common.base.Strings;

/**
 * Render a vertical list of histogram buckets with horizontal bar graphs showing the count in each
 * bucket.
 *
 * @param <T> The type of value the histogram counted, which this formatter will format using the
 *        provided {@code upperBoundValueFormatter} parameter.
 */
public class ConsoleHistogramFormatter<T> {
  private final Function<T, String> upperBoundValueFormatter;
  private final int maxWidth;

  public ConsoleHistogramFormatter(Function<T, String> upperBoundValueFormatter) {
    this(upperBoundValueFormatter, 20);
  }

  public ConsoleHistogramFormatter(Function<T, String> upperBoundValueFormatter, int maxWidth) {
    checkArgument(maxWidth > 1, "maxWidth must be at least 2.");
    this.upperBoundValueFormatter = checkNotNull(upperBoundValueFormatter);
    this.maxWidth = maxWidth;
  }

  public String format(Histogram<T> histogram) {
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
        T bucketUpperBound = histogram.bucketUpperBound(i);
        bucketLabel = String.format("<= %s", upperBoundValueFormatter.apply(bucketUpperBound));
      } else {
        if (lastBucket > 0) {
          T openLowerBoundValue = histogram.bucketUpperBound(i - 1);
          bucketLabel = String.format(">  %s", upperBoundValueFormatter.apply(openLowerBoundValue));
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
