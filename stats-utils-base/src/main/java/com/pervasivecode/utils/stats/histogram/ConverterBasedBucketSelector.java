package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Converter;

/**
 * A BucketSelector based on a {@link Converter} between upper-bound-values and bucket indices.
 *
 * @param <T> The type of upper bound value used by this BucketSelector.
 * @see BucketingSystem
 */
public class ConverterBasedBucketSelector<T> implements BucketSelector<T> {
  private final Integer numBuckets;
  private final Converter<T, Integer> converter;

  /**
   * Create a BucketSelector using a {@link Converter} that converts an upper-bound-value into a
   * bucket index and vice-versa.
   *
   * @param converter A converter that handles bucketing of values of type T, and that generates
   *        upper bound values when used in reverse with a bucket index. Note: the converter will
   *        not have to handle null values in either direction.
   * @param numBuckets The total number of buckets including the highest, unbounded bucket.
   */
  public ConverterBasedBucketSelector(Converter<T, Integer> converter, int numBuckets) {
    this.converter = checkNotNull(converter);
    checkArgument(numBuckets > 0, "numBuckets must be greater than 0.");
    this.numBuckets = numBuckets;
  }

  @Override
  public int numBuckets() {
    return numBuckets;
  }

  @Override
  public T bucketUpperBound(int index) {
    checkElementIndex(index, numBuckets);
    checkArgument(index < numBuckets - 1, "There is no upper bound for the last bucket.");
    return converter.reverse().convert(index);
  }

  @Override
  public int bucketIndexFor(T value) {
    checkNotNull(value, "Can't bucket a null value.");
    return converter.convert(value);
  }
}
