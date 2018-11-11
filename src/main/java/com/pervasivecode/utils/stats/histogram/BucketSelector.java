package com.pervasivecode.utils.stats.histogram;

import javax.annotation.Nonnull;

/**
 * This object determines which histogram bucket a particular value belongs in.
 *
 * @param <T> The type of value that this BucketSelector will handle by selecting an appropriate
 *        bucket index.
 */
public interface BucketSelector<T> extends BucketingSystem<T> {
  /**
   * Determine the index of the bucket that a specified value should be counted in.
   *
   * @param value The value to compare to bucket upper bounds values when identifying the
   *        appropriate bucket.
   * @return The bucket index for the specified value.
   */
  public int bucketIndexFor(@Nonnull T value);
}
