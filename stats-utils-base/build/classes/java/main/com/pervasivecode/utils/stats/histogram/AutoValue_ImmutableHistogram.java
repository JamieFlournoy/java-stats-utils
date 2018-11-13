

package com.pervasivecode.utils.stats.histogram;

import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ImmutableHistogram<T> extends ImmutableHistogram<T> {

  private final ImmutableList<Long> countByBucket;

  private final ImmutableList<T> bucketUpperBounds;

  private AutoValue_ImmutableHistogram(
      ImmutableList<Long> countByBucket,
      ImmutableList<T> bucketUpperBounds) {
    this.countByBucket = countByBucket;
    this.bucketUpperBounds = bucketUpperBounds;
  }

  @Override
  protected ImmutableList<Long> countByBucket() {
    return countByBucket;
  }

  @Override
  protected ImmutableList<T> bucketUpperBounds() {
    return bucketUpperBounds;
  }

  @Override
  public String toString() {
    return "ImmutableHistogram{"
         + "countByBucket=" + countByBucket + ", "
         + "bucketUpperBounds=" + bucketUpperBounds
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ImmutableHistogram) {
      ImmutableHistogram<?> that = (ImmutableHistogram<?>) o;
      return (this.countByBucket.equals(that.countByBucket()))
           && (this.bucketUpperBounds.equals(that.bucketUpperBounds()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= countByBucket.hashCode();
    h$ *= 1000003;
    h$ ^= bucketUpperBounds.hashCode();
    return h$;
  }

  static final class Builder<T> extends ImmutableHistogram.Builder<T> {
    private ImmutableList<Long> countByBucket;
    private ImmutableList<T> bucketUpperBounds;
    Builder() {
    }
    @Override
    protected ImmutableHistogram.Builder<T> setCountByBucket(ImmutableList<Long> countByBucket) {
      if (countByBucket == null) {
        throw new NullPointerException("Null countByBucket");
      }
      this.countByBucket = countByBucket;
      return this;
    }
    @Override
    protected ImmutableHistogram.Builder<T> setBucketUpperBounds(ImmutableList<T> bucketUpperBounds) {
      if (bucketUpperBounds == null) {
        throw new NullPointerException("Null bucketUpperBounds");
      }
      this.bucketUpperBounds = bucketUpperBounds;
      return this;
    }
    @Override
    protected ImmutableHistogram<T> autoBuild() {
      String missing = "";
      if (this.countByBucket == null) {
        missing += " countByBucket";
      }
      if (this.bucketUpperBounds == null) {
        missing += " bucketUpperBounds";
      }
      if (!missing.isEmpty()) {
        throw new IllegalStateException("Missing required properties:" + missing);
      }
      return new AutoValue_ImmutableHistogram<T>(
          this.countByBucket,
          this.bucketUpperBounds);
    }
  }

}
