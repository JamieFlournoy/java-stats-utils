package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * A thread-safe Histogram based on the {@link AtomicLongArray} class.
 *
 * @param <T> The type of value counted by this Histogram.
 */
public class ConcurrentHistogram<T> implements MutableHistogram<T> {
  private final AtomicLongArray bucketCounts;
  private final BucketSelector<T> bucketer;

  public ConcurrentHistogram(BucketSelector<T> bucketer) {
    this.bucketer = checkNotNull(bucketer);
    this.bucketCounts = new AtomicLongArray(bucketer.numBuckets());
  }

  @Override
  public int numBuckets() {
    return bucketer.numBuckets();
  }

  @Override
  public T bucketUpperBound(int index) {
    return bucketer.bucketUpperBound(index);
  }

  @Override
  public long countInBucket(int index) {
    return bucketCounts.get(index);
  }

  @Override
  public void countValue(T value) {
    checkNotNull(value);
    int bucketIndex = bucketer.bucketIndexFor(value);
    bucketCounts.incrementAndGet(bucketIndex);
  }
}
