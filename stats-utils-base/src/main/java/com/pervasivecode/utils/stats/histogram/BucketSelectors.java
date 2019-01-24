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
 *
 * @see BucketingSystem
 */
public class BucketSelectors {
  /**
   * Wrap a BucketSelector that handles one value type inside one that handles a different value
   * type, using a Converter that converts individual values from one type to the other.
   *
   * @param input The existing BucketSelector instance, which handles value type T. This is
   *        encapsulated inside a returned BucketSelector that handles value type V.
   * @param transformer A {@link Converter} that can change values of type T into V and values of
   *        type V into T.
   * @param <T> The value type of the existing input BucketSelector.
   * @param <V> The value type of the BucketSelector that this method will return.
   * @return A BucketSelector that can handle the value type V.
   */
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
   * Example: arguments {@code minPower=2} and {@code numBuckets=5} would generate upper bound
   * values 4, 8, 16, and 32.
   *
   * @param minPower The smallest power of 2 to use when generating upper bound values.
   * @param numBuckets The number of buckets that the BucketSelector should provide.
   * @return A {@code BucketSelector<Long>} instance.
   */
  public static BucketSelector<Long> powerOf2LongValues(int minPower, int numBuckets) {
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
   * Get a BucketSelector that has upper bound values that are {@code double} values that are part
   * of an exponential series. Each bucket value's exponent is 1.0 plus the exponent of the previous
   * bucket value.
   * <p>
   * Example: arguments {@code base=5.0}, {@code minPower=0.0}, and {@code numBuckets=5} would
   * generate base and exponent values of 5<sup>0</sup>, 5<sup>1</sup>, 5<sup>2</sup>, and
   * 5<sup>3</sup>, so the upper bound values would be 1.0, 5.0, 25.0, and 125.0.
   *
   * @param base The base that will be raised to the specified exponents to generate upper bound
   *        values.
   * @param minPower The smallest exponent to use when generating upper bound values.
   * @param numBuckets The number of buckets into which values should be counted.
   *
   * @return A {@code BucketSelector<Double>} instance.
   */
  public static BucketSelector<Double> exponential(double base, double minPower, int numBuckets) {
    double logOfBase = Math.log(base);

    Converter<Double, Integer> converter = new Converter<>() {
      @Override
      protected Integer doForward(Double value) {
        if (value <= 0) {
          return 0;
        }
        if (value == Double.POSITIVE_INFINITY) {
          return numBuckets - 1;
        }
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
   * Get a BucketSelector that has upper bound values that are {@code long} values that are part
   * of an exponential series. Each bucket value's exponent is 1.0 plus the exponent of the previous
   * bucket value.
   * <p>
   * Example: arguments {@code base=5.0}, {@code minPower=0.0}, and {@code numBuckets=5} would
   * generate base and exponent values of 5<sup>0</sup>, 5<sup>1</sup>, 5<sup>2</sup>, and
   * 5<sup>3</sup>, so the upper bound values would be 1, 5, 25, and 125.
   *
   * @param base The base that will be raised to the specified exponents to generate upper bound
   *        values.
   * @param minPower The smallest exponent to use when generating upper bound values.
   * @param numBuckets The number of buckets into which values should be counted.
   *
   * @return A {@code BucketSelector<Long>} instance.
   */
  public static BucketSelector<Long> exponentialLong(double base, double minPower, int numBuckets) {
    Converter<Long, Double> converter = new Converter<Long, Double>() {
      @Override
      protected Double doForward(Long a) {
        return a.doubleValue();
      }

      @Override
      protected Long doBackward(Double b) {
        if (b.isInfinite()) {
          return b > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
        return b.longValue();
      }

    };
    return BucketSelectors.transform(BucketSelectors.exponential(base, minPower, numBuckets),
        converter);
  }

  /**
   * Get a BucketSelector that has upper bound values that are evenly distributed between a smallest
   * and largest value.
   * <p>
   * Example: arguments {@code lowestUpperBound=0}, {@code highestUpperBound=20}, and
   * {@code numBuckets=6} would generate upper bound values 0, 5, 10, 15, and 20.
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
