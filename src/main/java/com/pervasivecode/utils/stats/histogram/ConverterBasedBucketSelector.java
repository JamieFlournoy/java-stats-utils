package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Converter;

public class ConverterBasedBucketSelector<T> implements BucketSelector<T> {
  private final Integer numBuckets;
  private final Converter<T, Integer> converter;

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
