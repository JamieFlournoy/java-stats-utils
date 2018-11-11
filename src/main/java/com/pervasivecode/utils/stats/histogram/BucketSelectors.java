package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * BucketSelector factory methods for basic bucketing strategies.
 */
public class BucketSelectors {

  /**
   * Get a BucketSelector that has upper bound values that are even powers of 2.
   * <p>
   * Example: upper bound values 4, 8, 16, 32.
   *
   * @param minPower The smallest power of 2 to use when generating upper bound values.
   * @param numBuckets The number of buckets.
   * @return A {@code BucketSelector<Long>} instance.
   */
  // TODO replace these with a ConvertingBucketSelector based on a com.google.common.base.Converter
  public static BucketSelector<Long> powerOf2LongValues(int minPower, final int numBuckets) {
    checkArgument(minPower >= 0, "minPower must be non-negative.");
    checkArgument(numBuckets > 0, "numBuckets must be greater than 0.");
    final int lastBucketIndex = numBuckets - 1;
    return new BucketSelector<>() {
      @Override
      public int numBuckets() {
        return numBuckets;
      }

      @Override
      public int bucketIndexFor(Long value) {
        long longValue = checkNotNull(value, "Can't bucket a null value.");
        long currentBucketMaxValue = 1L << minPower;
        for (int i = 0; i < lastBucketIndex; i++) {
          if (longValue <= currentBucketMaxValue) {
            return i;
          }
          currentBucketMaxValue <<= 1;
        }
        return lastBucketIndex;
      }

      @Override
      public Long bucketUpperBound(int index) {
        checkElementIndex(index, numBuckets);
        checkArgument(index < lastBucketIndex, "There is no upper bound for the last bucket.");
        long maxValue = 1L << ((long) index + (long) minPower);
        return maxValue;
      }
    };
  }

  /**
   * Get a BucketSelector that has upper bound values that are evenly distributed between a smallest
   * and largest value.
   * <p>
   * Example: 0, 5, 10, 15, 20.
   *
   * @param lowestUpperBound The upper-bound value for the first bucket (index 0).
   * @param highestUpperBound The upper-bound value for the next-to-last bucket
   *        ({@code index (numBuckets - 2)}).
   * @param numBuckets The total number of buckets.
   * @return A {@code BucketSelector<Long>} instance.
   */
  public static BucketSelector<Long> linearLongValues(long lowestUpperBound, long highestUpperBound,
      int numBuckets) {
    BigDecimal bucketWidth = BigDecimal.valueOf(highestUpperBound - lowestUpperBound)
        .divide(BigDecimal.valueOf(numBuckets - 2), RoundingMode.HALF_EVEN);

    return new BucketSelector<>() {
      @Override
      public int numBuckets() {
        return numBuckets;
      }

      @Override
      public Long bucketUpperBound(int index) {
        return lowestUpperBound + bucketWidth.multiply(BigDecimal.valueOf(index)).longValue();
      }

      @Override
      public int bucketIndexFor(Long value) {
        checkNotNull(value);
        if (value <= lowestUpperBound) {
          return 0;
        }
        if (value > highestUpperBound) {
          return numBuckets - 1;
        }
        BigDecimal relativized = BigDecimal.valueOf(value - lowestUpperBound);
        return relativized.divide(bucketWidth, RoundingMode.CEILING).intValue();
      }
    };
  }
}
