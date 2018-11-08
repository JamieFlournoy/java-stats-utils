package com.pervasivecode.utils.stats.histogram;

import java.util.function.Function;

public class Histograms {
  /**
   * Transform a histogram's upper bound values using a Function.
   *
   * @param input The histogram to transform.
   * @param transformation A function that will transform one upper bound value.
   * @param <T> The type of value counted by the input Histogram.
   * @param <V> The type of value emitted by the {@code transformation} {@link Function}.
   * @return A Histogram wrapping the input histogram, whose bucketUpperBound values are the
   *         transformed version of the input histogram's bucketUpperBound values.
   */
  public static <T, V> Histogram<V> transformValues(Histogram<T> input,
      Function<T, V> transformation) {
    return new Histogram<>() {
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
