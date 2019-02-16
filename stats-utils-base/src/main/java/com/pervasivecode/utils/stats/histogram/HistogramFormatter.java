package com.pervasivecode.utils.stats.histogram;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.concurrent.Immutable;
import com.pervasivecode.utils.stats.HorizontalBarGraph;

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
@Immutable
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
  }

  public String format(Histogram<T> histogram) {
    requireNonNull(histogram, "histogram is required.");
    return formatInternal(ImmutableHistogram.from(histogram));
  }

  private String formatInternal(ImmutableHistogram<T> histogram) {
    final int bucketCount = histogram.numBuckets();

    final ArrayList<String> bucketLabels = new ArrayList<>(bucketCount);
    final ArrayList<Long> magnitudes = new ArrayList<>(bucketCount);
    final ArrayList<String> formattedMagnitudes = new ArrayList<>(bucketCount);

    final long totalCount = histogram.totalCount();
    final BiFunction<Long, Long, String> bucketCountFormatter = format.bucketCountFormatter();
    final int lastBucket = bucketCount - 1;
    for (int i = 0; i <= lastBucket; i++) {
      bucketLabels.add(getBucketLabel(i, lastBucket, histogram));
      final long count = histogram.countInBucket(i);
      magnitudes.add(count);
      formattedMagnitudes.add(bucketCountFormatter.apply(count, totalCount));
    }

    final HorizontalBarGraph graph = HorizontalBarGraph.builder() //
        .setWidth(format.maxWidth()) //
        .setNumRows(bucketCount) //
        .setLabels(bucketLabels) //
        .setMagnitudes(magnitudes) //
        .setFormattedMagnitudes(formattedMagnitudes) //
        .build();
    return graph.format();
  }

  private String getBucketLabel(int bucketIndex, int lastBucket, Histogram<T> histogram) {
    final Function<T, String> upperBoundValueFormatter = format.upperBoundValueFormatter();
    final int i = bucketIndex;
    if (i < lastBucket) {
      final T bucketUpperBound = histogram.bucketUpperBound(i);
      return String.format("<= %s", upperBoundValueFormatter.apply(bucketUpperBound));
    } else {
      if (lastBucket > 0) {
        final T openLowerBoundValue = histogram.bucketUpperBound(i - 1);
        return String.format(">  %s", upperBoundValueFormatter.apply(openLowerBoundValue));
      } else {
        return format.labelForSingularBucket();
      }
    }
  }
}
