package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.SortedSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

/**
 * This BucketSelector counts values in buckets that have an irregular set of bucket upper bound
 * values, such as {1, 5, 7}, that are most easily expressed explicitly, rather than by a formula
 * that generates a series of upper bound values.
 *
 * @see BucketingSystem
 */
public class IrregularSetBucketSelector<T> implements BucketSelector<T> {
  static final String NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE =
      "There is no upper bound for the last bucket.";

  private final ImmutableSortedSet<T> bucketMaxValueSet;
  private final ImmutableList<T> bucketMaxValueList;
  private final int lastMaxValueIndex;

  /**
   * Create a IrregularSetBucketSelector from a set of upper-bound values.
   *
   * @param upperBoundValueSet The upper bound values for the buckets. (There will also be one more
   *        bucket that has no upper bound.)
   */
  public IrregularSetBucketSelector(SortedSet<T> upperBoundValueSet) {
    this.bucketMaxValueSet = ImmutableSortedSet.copyOf(checkNotNull(upperBoundValueSet));
    this.bucketMaxValueList = ImmutableList.copyOf(upperBoundValueSet.iterator());
    this.lastMaxValueIndex = bucketMaxValueList.size();
  }

  @Override
  public int numBuckets() {
    return this.lastMaxValueIndex + 1;
  }


  @Override
  public int bucketIndexFor(T value) {
    return bucketMaxValueSet.headSet(value, false).size();
  }

  @Override
  public T bucketUpperBound(int bucketIndex) {
    checkArgument(bucketIndex < lastMaxValueIndex, NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE);
    return bucketMaxValueList.get(bucketIndex);
  }
}
