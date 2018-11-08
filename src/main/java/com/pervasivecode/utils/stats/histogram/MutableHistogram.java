package com.pervasivecode.utils.stats.histogram;

/**
 * This is a mutable version of the Histogram interface, adding a method for counting additional
 * values.
 * 
 * @param <T> The type of value that is being counted.
 */
public interface MutableHistogram<T> extends Histogram<T> {

  /**
   * Count the specified value, by assigning it to a bucket and incrementing the stored count of
   * values that belong to that bucket.
   * 
   * @param value The value to count. This may not be null.
   */
  public void countValue(T value);
}
