package com.pervasivecode.utils.stats.histogram;

/**
 * This is a data structure that holds frequency counts of values for use in a histogram. Since a
 * histogram shows the number values within specified ranges, the structure only stores counts all
 * values in each range.
 * <p>
 * "Buckets" (the ranges of counted values) are identified by an inclusive upper bound value. Values
 * from negative infinity (or the equivalent in the given type) through the lowest upper bound value
 * go into the first bucket; values greater than the highest upper bound value and up to positive
 * infinity (or the equivalent) go in the last bucket.
 * <p>
 * Example: A Histogram of String values has 5 buckets, with upper bound values of "Apple", "Hewlett
 * Packard", "Sun", and "Wang" (the last bucket never has an upper bound value, so there is no fifth
 * upper bound value).
 * <p>
 * In this example histogram, here is how elements would be counted:
 * <ul>
 * <li>A value "Acorn" would have gone in the first bucket since it's less-than-or-equal-to
 * "Apple".</li>
 * <li>If the second bucket has counted 5 values, that means that there must have been five values
 * counted that were greater than "Apple" and less-than-or-equal-to "Hewlett Packard".</li>
 * <li>A value "Tandem" would have gone in the fourth bucket since it's greater than "Sun" but less
 * than or equal to "Wang".</li>
 * <li>"Zenith" would go in the last (5th) bucket, since it's greater than the last
 * upper-bound-value, "Wang".</li>
 * </ul>
 * 
 * @param <T> The type of value that is being counted.
 */
public interface Histogram<T> extends BucketingSystem<T> {
  /**
   * Get the number of elements that were counted in the specified bucket. The first bucket has an
   * index of 0.
   * 
   * @param index The bucket number to examine.
   * @return The count of values in the specified bucket.
   */
  public long countInBucket(int index);

}
