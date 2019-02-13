package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkState;
import java.util.List;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableList;

/**
 * An immutable representation of a Histogram.
 *
 * @param <T> The type of value counted by this Histogram.
 *
 * @see BucketingSystem
 */
@AutoValue
@Immutable // <- so that static analysis tools will verify that it really is immutable
public abstract class ImmutableHistogram<T> implements Histogram<T> {
  static final String NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE =
      "There is no upper bound for the last bucket.";

  protected abstract List<Long> countByBucket();

  protected abstract List<T> bucketUpperBounds();

  @Override
  public int numBuckets() {
    return countByBucket().size();
  }

  @Override
  public long countInBucket(int index) {
    return countByBucket().get(index);
  }

  @Override
  public T bucketUpperBound(int index) {
    checkElementIndex(index, numBuckets());
    checkArgument(index < (numBuckets() - 1), NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE);
    return bucketUpperBounds().get(index);
  }

  /**
   * Get the maximum bucket count value.
   *
   * @return The largest bucket count value.
   */
  @Memoized
  public long maxCount() {
    return countByBucket().stream().max((a, b) -> a.compareTo(b)).get();
  }

  /**
   * Get a total of the bucket counts.
   *
   * @return The total of all bucket counts.
   */
  @Memoized
  public long totalCount() {
    return countByBucket().stream().reduce(0L, (a, b) -> a + b);
  }

  /**
   * Obtain a builder that allows construction of a new instance.
   *
   * @param <V> The type of value counted by this Histogram.
   * @return A new, empty Builder instance.
   */
  public static <V> ImmutableHistogram.Builder<V> builder() {
    return new AutoValue_ImmutableHistogram.Builder<>();
  }

  /**
   * Obtain a builder that allows construction of a new instance, prepopulated with values from
   * another instance.
   *
   * @param histogram The instance whose values should be used to populate the new Builder instance.
   * @param <V> The type of value counted by this Histogram.
   * @return a new Builder instance with the same values as the {@code histogram} parameter.
   */
  public static <V> ImmutableHistogram.Builder<V> builder(ImmutableHistogram<V> histogram) {
    Objects.requireNonNull(histogram, "The input histogram cannot be null.");
    return new AutoValue_ImmutableHistogram.Builder<V>()
        .setBucketUpperBounds(histogram.bucketUpperBounds())
        .setCountByBucket(histogram.countByBucket());
  }

  /**
   * An object that can be used to create an {@link ImmutableHistogram}.
   * @param <T> The type of value counted by the ImmutableHistogram that this Builder will make.
   */
  @AutoValue.Builder
  public abstract static class Builder<T> {
    /**
     * Set the count of values per bucket, all at once. This list's size defines the number of
     * buckets of the histogram.
     *
     * @param countByBucket The list of value-counts, in ascending bucket-index order.
     * @return A builder that can be used to finish creating an ImmutableHistogram instance.
     */
    public abstract Builder<T> setCountByBucket(List<Long> countByBucket);

    /**
     * Set the upper bounds of each of the buckets that has an upper bound, all at once. This list's
     * size must be one less than the size of the list provided to {@link #setCountByBucket(List)}.
     *
     * @param bucketUpperBounds The list of bucket upper bounds, in ascending bucket-index order.
     * @return A builder that can be used to finish creating an ImmutableHistogram instance.
     */
    public abstract Builder<T> setBucketUpperBounds(List<T> bucketUpperBounds);

    protected abstract ImmutableHistogram<T> buildInternal();

    /**
     * Validate the countByBucket and bucketUpperBounds values and create an ImmutableHistogram.
     *
     * @return An immutable Histogram with the values specified in the
     *         {@link #setCountByBucket(List)} and {@link #setBucketUpperBounds(List)} methods.
     * @throws IllegalStateException if countByBucket is empty, or if the number of upper bound
     *         values is anything other than one less than the number of counts by bucket.
     */
    public ImmutableHistogram<T> build() {
      ImmutableHistogram<T> unvalidated = buildInternal();

      if (unvalidated.countByBucket().isEmpty()) {
        throw new IllegalStateException("countByBucket cannot be empty");
      }
      int numUpperBounds = unvalidated.bucketUpperBounds().size();
      int expectedNumUpperBounds = unvalidated.countByBucket().size() - 1;
      checkState(numUpperBounds == expectedNumUpperBounds,
            "Wrong number of bucketUpperBounds values. (Expected %s, got %s)",
                expectedNumUpperBounds, numUpperBounds);

      // Ensure that the countByBucket and bucketUpperBounds lists are really immutable.
      if (unvalidated.countByBucket() instanceof ImmutableList &&
          unvalidated.bucketUpperBounds() instanceof ImmutableList) {
        return unvalidated;
      }

      final ImmutableList<Long> immutableCountByBucket;
      try {
        immutableCountByBucket = ImmutableList.copyOf(unvalidated.countByBucket());
      } catch (NullPointerException npe) {
        throw new IllegalStateException("countByBucket cannot contain any null values.", npe);
      }

      final ImmutableList<T> immutableUpperBounds;
      try {
        immutableUpperBounds = ImmutableList.copyOf(unvalidated.bucketUpperBounds());
      } catch (NullPointerException npe) {
        throw new IllegalStateException("upperBounds cannot contain any null values.", npe);
      }

      return ImmutableHistogram.builder(unvalidated) //
          .setCountByBucket(immutableCountByBucket) //
          .setBucketUpperBounds(immutableUpperBounds) //
          .build();
    }
  }

  /**
   * Make an immutable copy of another histogram with the same value type, size, bucket upper bounds
   * and value counts.
   * <p>
   * This method will always make a copy, as opposed to {@link ImmutableHistogram#from(Histogram)}
   * which will return the input histogram if it was already an {@code ImmutableHistogram}.
   *
   * @param histogram The histogram to copy.
   * @param <V> The type of value counted by this Histogram.
   * @return An immutable copy of the histogram passed to this method.
   */
  public static <V> ImmutableHistogram<V> copyOf(Histogram<V> histogram) {
    ImmutableList.Builder<V> maxValuesBuilder = ImmutableList.builder();
    ImmutableList.Builder<Long> bucketValuesBuilder = ImmutableList.builder();
    int lastBucketIndex = histogram.numBuckets() - 1;
    for (int i = 0; i <= lastBucketIndex; i++) {
      bucketValuesBuilder.add(histogram.countInBucket(i));
      if (i < lastBucketIndex) {
        maxValuesBuilder.add(histogram.bucketUpperBound(i));
      }
    }
    return ImmutableHistogram.<V>builder().setBucketUpperBounds(maxValuesBuilder.build())
        .setCountByBucket(bucketValuesBuilder.build()).build();
  }

  /**
   * Return an ImmutableHistogram with the same contents as the {@code histogram} parameter. If the
   * {@code histogram} parameter is already an instance of {@code ImmutableHistogram}, it is
   * returned. (This is different from {@link #copyOf(Histogram)}, which will always return a copy.}
   *
   * @param histogram The {@code Histogram} to return as an {@code ImmutableHistogram}.
   * @return A copy of the input histogram, or (if the input histogram is already an
   *         {@code ImmutableHistogram}, the input histogram.
   */
  public static <V> ImmutableHistogram<V> from(Histogram<V> histogram) {
    if (histogram instanceof ImmutableHistogram) {
      return (ImmutableHistogram<V>) histogram;
    }
    return ImmutableHistogram.copyOf(histogram);
  }
}
