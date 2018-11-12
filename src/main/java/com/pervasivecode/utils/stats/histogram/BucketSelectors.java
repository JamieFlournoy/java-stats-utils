package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.math.RoundingMode.CEILING;
import static java.math.RoundingMode.HALF_EVEN;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.google.common.base.Converter;

/**
 * BucketSelector factory methods for basic bucketing strategies.
 */
public class BucketSelectors {
  public static <T, V> BucketSelector<V> transform(BucketSelector<T> input,
      Converter<V, T> transformer) {
    return new BucketSelector<V>() {
      @Override
      public int numBuckets() {
        return input.numBuckets();
      }

      @Override
      public V bucketUpperBound(int index) {
        return transformer.reverse().convert(input.bucketUpperBound(index));
      }

      @Override
      public int bucketIndexFor(V value) {
        return input.bucketIndexFor(transformer.convert(value));
      }
    };
  }


  /**
   * Get a BucketSelector that has upper bound values that are consecutive whole-number powers of 2.
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
   * Get a BucketSelector that has upper bound values that are {@code <T>} instances that are part
   * of an exponential series.
   * <p>
   * Example: upper bound values 1, 5, 25, 125
   *
   * @param base The base that will be raised to the specified exponents to generate upper bound
   *        values.
   * @param minPower The smallest exponent to use when generating upper bound values.
   * @param numBuckets The number of buckets into which values should be counted.
   * @param <T> The type of measurement described by the bucket upper bounds. Example: Length.
   *
   * @return A {@code BucketSelector<Quantity<T>>} instance.
   */
  public static BucketSelector<Double> exponential(double base, double minPower, int numBuckets) {
    double logOfBase = Math.log(base);

    Converter<Double, Integer> converter = new Converter<>() {
      @Override
      protected Integer doForward(Double value) {
        double logOfValue = Math.log(value.doubleValue());

        // Use BigDecimal and round carefully, to work around Double precision limitations.
        // Example: Math.log(125)/Math.log(5) => 3.0000000000000004 (should be exactly 3).
        BigDecimal indexAsBig = BigDecimal.valueOf(logOfValue) //
            .divide(BigDecimal.valueOf(logOfBase), 12, HALF_EVEN) //
            .subtract(BigDecimal.valueOf(minPower));

        int indexIgnoringNumBuckets = indexAsBig.setScale(0, CEILING).intValue();
        return Math.min(indexIgnoringNumBuckets, numBuckets - 1);
      }

      @Override
      protected Double doBackward(Integer index) {
        return

        Math.pow(base, minPower + index);
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
