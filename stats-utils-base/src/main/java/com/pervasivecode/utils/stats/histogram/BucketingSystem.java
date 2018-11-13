package com.pervasivecode.utils.stats.histogram;

import javax.annotation.Nonnull;

/**
 * Common methods for things that put values into a fixed number of adjacent intervals.
 * <p>
 * Bucketing is defined here as selecting an interval from a series of adjacent intervals that
 * follow these properties:
 * <ul>
 * <li>If there is only one interval, it is unbounded, so all values are contained in it.</li>
 * <li>If there are at least two intervals, there is a half-closed right-bounded interval that
 * starts from negative infinity and includes an upper bound, and an open left-bounded interval that
 * includes all of the values larger than the largest upper bound value.</li>
 * <li>If there are at least three intervals, then there are additional half-open bounded intervals
 * that include values between the lowest upper bound (exclusive) and the highest upper bound
 * (inclusive).</li>
 * <li>In all cases, the intervals are adjacent, so every possible value of the parameterized type
 * {@code <T>} falls into exactly one interval.</li>
 * <li>Intervals are numbered starting from 0, arranged as they would appear on a number line. Thus,
 * the first interval has no left bound, and the last interval has no right bound.
 * </ul>
 * Example: a bucketing system of Integer is defined using upper bounds of 0 and 100. This means
 * that there are three buckets: (negative infinity - 0], (0 - 100], and (100 - infinity).
 * <ul>
 * <li>The first interval contains values such as -1, Integer.MIN_VALUE, and 0.</li>
 * <li>The second interval contains values such as 1, 99, and 100.</li>
 * <li>The third interval contains valuies such as 101 and Integer.MAX_VALUE.</li>
 * </ul>
 */
public interface BucketingSystem<T> {
  /**
   * Get the total number of buckets. This will not be smaller than 1.
   *
   * @return The number of buckets.
   */
  public int numBuckets();


  /**
   * Get the upper-bound value of the specified bucket.
   * <p>
   * The first bucket has an index of 0. The last bucket has no upper bound, so passing the last
   * index value in {@code index} is not allowed and will result in an IllegalArgumentException
   * being thrown. In other words, if there are 10 buckets in a given instance, there are only 9
   * upper bound values, so the valid index values are 0 through 8 inclusive.
   * <p>
   * A BucketingSystem with only one bucket is valid (all values fall in that bucket), but would
   * have no upper bound values, so this method would always throw an IllegalArgumentException (for
   * {@code index} value 0) or an IndexOutOfBoundsException (for all other values of {@code index}).
   *
   * @param index The bucket index to examine. This must be a value from zero through the
   *        next-to-last bucket index.
   * @return The upper bound value of the specified bucket. This will not be null.
   * @throws IllegalArgumentException if {@code index} indicates the last bucket.
   */
  @Nonnull
  public T bucketUpperBound(int index);

}
