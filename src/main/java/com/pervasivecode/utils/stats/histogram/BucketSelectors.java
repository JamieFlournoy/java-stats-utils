package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.google.common.base.Converter;


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
  public static BucketSelector<Long> powerOf2LongValues(int minPower, final int numBuckets) {
    checkArgument(minPower >= 0, "minPower must be non-negative.");

    Converter<Long, Integer> converter = new Converter<>() {
      @Override
      protected Integer doForward(Long value) {
        long longValue = value.longValue();
        long currentBucketMaxValue = 1L << minPower;
        for (int i = 0; i < numBuckets - 1; i++) {
          if (longValue <= currentBucketMaxValue) {
            return i;
          }
          currentBucketMaxValue <<= 1;
        }
        return numBuckets - 1;
      }

      @Override
      protected Long doBackward(Integer index) {
        return 1L << ((long) index + (long) minPower);
      }

    };

    return new ConverterBasedBucketSelector<>(converter, numBuckets);
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

    Converter<Long, Integer> converter = new Converter<>() {
      @Override
      protected Integer doForward(Long value) {
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

      @Override
      protected Long doBackward(Integer index) {
        return lowestUpperBound + bucketWidth.multiply(BigDecimal.valueOf(index)).longValue();
      }
    };

    return new ConverterBasedBucketSelector<>(converter, numBuckets);
  }
}
