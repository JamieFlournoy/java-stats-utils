package com.pervasivecode.utils.stats.histogram;

import java.util.Objects;
import java.util.function.Function;

/**
 * Utility methods for working with Histograms.
 */
public class Histograms {
  private Histograms() {}

  /**
   * Transform a histogram's upper bound values using a Function.
   *
   * @param input The histogram to transform.
   * @param transformation A function that will transform one upper bound value.
   * @param <T> The type of value counted by the input Histogram.
   * @param <V> The type of value emitted by the {@code transformation} {@link Function}.
   * @return A Histogram wrapping the input histogram, whose bucketUpperBound values are the
   *         transformed version of the input histogram's bucketUpperBound values.
   * @see BucketingSystem
   */
  public static <T, V> Histogram<V> transformValues(Histogram<T> input,
      Function<T, V> transformation) {
    Objects.requireNonNull(input, "The input histogram parameter is required.");
    Objects.requireNonNull(transformation, "The transformation function parameter is required.");

    return new Histogram<V>() {
      @Override
      public int numBuckets() {
        return input.numBuckets();
      }

      @Override
      public V bucketUpperBound(int index) {
        return transformation.apply(input.bucketUpperBound(index));
      }

      @Override
      public long countInBucket(int index) {
        return input.countInBucket(index);
      }
    };
  }
}
