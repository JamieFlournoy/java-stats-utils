package com.pervasivecode.utils.stats.histogram;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;

/**
 * Configuration for a {@link HistogramFormatter}.
 *
 * @param <T> The type of value that this HistogramFormat can handle.
 * @see HistogramBucketCountFormatters
 */
@AutoValue
@Immutable
public abstract class HistogramFormat<T> {
  protected HistogramFormat() {}

  /**
   * A function that formats a bucket upper bound value as a String.
   *
   * @return The value formatter function.
   */
  public abstract Function<T, String> upperBoundValueFormatter();

  /**
   * The string label to use when the Histogram contains exactly one bucket.
   *
   * @return The label to use for a single-bucket histogram.
   */
  public abstract String labelForSingularBucket();

  /**
   * A BiFunction that specifies how a bucket count value should be displayed.
   * <p>
   * This could be a percentage of the total number of items counted by the histogram across all
   * buckets, or just the bucket count itself if a percentage is not desired.
   *
   * @return bucketCountFormatter A {@link BiFunction} that takes a {@code Long} bucket count, a
   *         {@code Long} total of all bucket counts, and returns a {@code String} formatted
   *         representation.
   */
  public abstract BiFunction<Long, Long, String> bucketCountFormatter();

  /**
   * The maximum width of the formatted representation, in characters.
   *
   * @return The maximum width of the bar graph.
   */
  public abstract int maxWidth();

  /**
   * Create a new {@link HistogramFormat.Builder} instance.
   * <p>
   * This builder is mostly unconfigured, except for the following default values:
   * <ul>
   * <li>labelForSingularBucket = "?"</li>
   * </ul>
   *
   * @param <T> The type of value handled by the HistogramFormat that this Builder will make.
   * @return The new {@link HistogramFormat.Builder} instance.
   */
  public static <T> HistogramFormat.Builder<T> builder() {
    return new AutoValue_HistogramFormat.Builder<T>().setLabelForSingularBucket("?");
  }

  /**
   * This object is used to construct a HistogramFormat instance. See {@link HistogramFormat} for
   * explanations of what these values mean.
   *
   * @param <T> The type of value that the constructed HistogramFormat will be able to handle.
   */
  @AutoValue.Builder
  public static abstract class Builder<T> {
    protected Builder() {}

    public abstract HistogramFormat.Builder<T> setUpperBoundValueFormatter(
        Function<T, String> upperBoundValueFormatter);

    public abstract HistogramFormat.Builder<T> setLabelForSingularBucket(
        String labelForSingularBucket);

    public abstract HistogramFormat.Builder<T> setBucketCountFormatter(
        BiFunction<Long, Long, String> bucketCountFormatter);

    public abstract HistogramFormat.Builder<T> setMaxWidth(int maxWidth);

    public abstract HistogramFormat<T> build();
  }
}
