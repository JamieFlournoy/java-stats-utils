package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;
import java.util.function.Function;

/**
 * A BucketSelector based on a pair of {@link Function}s, one of which converts values into bucket
 * indices, the other of which converts bucket indices into upper-bound values.
 *
 * @param <T> The type of upper bound value used by this BucketSelector.
 * @see BucketingSystem
 */
public final class FunctionBasedBucketSelector<T> implements BucketSelector<T> {
  private final Integer numBuckets;
  private final Function<T, Integer> valueToBucketIndexFunction;
  private final Function<Integer, T> bucketIndexToUpperBoundFunction;

  /**
   * Create a BucketSelector using a {@link Function} that converts an upper-bound value into a
   * bucket index, and another {@link Function} that converts a bucket index into an upper-bound
   * value.
   * <p>
   * Note: neither function will have to handle null values.
   *
   * @param valueToBucketIndexFunction A function that selects a bucket index for a given value of
   *        type T.
   * @param bucketIndexToUpperBoundFunction A function that returns the upper-bound value for a
   *        given bucket index.
   * @param numBuckets The total number of buckets including the highest, unbounded bucket.
   */
  public FunctionBasedBucketSelector(
      Function<T, Integer> valueToBucketIndexFunction,
      Function<Integer, T> bucketIndexToUpperBoundFunction,
      int numBuckets) {
    checkArgument(numBuckets > 0, "numBuckets must be greater than 0.");
    this.numBuckets = numBuckets;
    this.valueToBucketIndexFunction = requireNonNull(valueToBucketIndexFunction);
    this.bucketIndexToUpperBoundFunction = requireNonNull(bucketIndexToUpperBoundFunction);
  }

  @Override
  public int numBuckets() {
    return numBuckets;
  }

  @Override
  public T bucketUpperBound(int index) {
    checkElementIndex(index, numBuckets);
    checkArgument(index < numBuckets - 1, "There is no upper bound for the last bucket.");
    return bucketIndexToUpperBoundFunction.apply(index);
  }

  @Override
  public int bucketIndexFor(T value) {
    checkNotNull(value, "Can't bucket a null value.");
    return valueToBucketIndexFunction.apply(value);
  }

  // TODO equals and hashcode.
}
