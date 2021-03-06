package com.pervasivecode.utils.stats.histogram;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.math.RoundingMode.CEILING;
import static java.math.RoundingMode.HALF_EVEN;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * BucketSelector factory methods for basic bucketing strategies.
 *
 * @see BucketingSystem
 */
public class BucketSelectors {
  private BucketSelectors() {}

  /**
   * Wrap a BucketSelector that handles one value type inside one that handles a different value
   * type, using a pair of {@link Function}s that convert individual values from one type to the
   * other.
   *
   * @param input The existing BucketSelector instance, which handles value type T. This is
   *        encapsulated inside a returned BucketSelector that handles value type V.
   * @param convertToWrappedType A {@link Function} that can change values of type values of type V
   *        into values of type T.
   * @param convertFromWrappedType A {@link Function} that can change values of type values of type
   *        T into values of type V.
   * @param <T> The value type of the existing input BucketSelector.
   * @param <V> The value type of the BucketSelector that this method will return.
   * @return A BucketSelector that can handle the value type V.
   */
  public static <T, V> BucketSelector<V> transform(BucketSelector<T> input,
      Function<V, T> convertToWrappedType, Function<T, V> convertFromWrappedType) {
    return new BucketSelector<V>() {
      @Override
      public int numBuckets() {
        return input.numBuckets();
      }

      @Override
      public V bucketUpperBound(int index) {
        return convertFromWrappedType.apply(input.bucketUpperBound(index));
      }

      @Override
      public int bucketIndexFor(V value) {
        return input.bucketIndexFor(convertToWrappedType.apply(value));
      }
    };
  }

  /**
   * Get a BucketSelector that has upper bound values that are consecutive whole-number powers of 2.
   * <p>
   * Example: arguments {@code minPower=2} and {@code numBuckets=5} would generate upper bound
   * values 4, 8, 16, and 32.
   *
   * @param minPower The smallest power of 2 to use when generating upper bound values. (This must
   *        be non-negative, since a long value can't be fractional nor have a fractional
   *        component.)
   * @param numBuckets The number of buckets that the BucketSelector should provide.
   * @return A {@code BucketSelector<Long>} instance.
   * @throws IllegalArgumentException if minPower is negative.
   */
  public static BucketSelector<Long> powerOf2LongValues(int minPower, int numBuckets) {
    checkArgument(minPower >= 0, "minPower must be non-negative. "
        + "Use exponential(double, double, numBuckets) if you need buckets for fractional values.");

    Function<Long, Integer> valueToBucketIndexFunction = (value) -> {
      long longValue = value.longValue();
      long currentBucketMaxValue = 1L << minPower;
      for (int i = 0; i < numBuckets - 1; i++) {
        if (longValue <= currentBucketMaxValue) {
          return i;
        }
        currentBucketMaxValue <<= 1;
      }
      return numBuckets - 1;
    };

    Function<Integer, Long> bucketIndexToUpperBoundFunction = (index) -> {
      return 1L << ((long) index + (long) minPower);
    };

    return new FunctionBasedBucketSelector<>(valueToBucketIndexFunction,
        bucketIndexToUpperBoundFunction, numBuckets);
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
    checkArgument(base >= 0.0, "base cannot be negative.");
    double logOfBase = Math.log(base);

    Function<Double, Integer> valueToBucketIndexFunction = (value) -> {
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
    };

    Function<Integer, Double> bucketIndexToUpperBoundFunction =
        (index) -> Math.pow(base, minPower + index);

    return new FunctionBasedBucketSelector<>(valueToBucketIndexFunction,
        bucketIndexToUpperBoundFunction, numBuckets);
  }

  /**
   * Get a BucketSelector that has upper bound values that are {@code long} values that are part of
   * an exponential series. Each bucket value's exponent is 1.0 plus the exponent of the previous
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
    return BucketSelectors.transform(BucketSelectors.exponential(base, minPower, numBuckets),
        (a) -> a.doubleValue(), (b) -> b.longValue());
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

    Function<Long, Integer> valueToBucketIndexFunction = (value) -> {
      checkNotNull(value);
      if (value <= lowestUpperBound) {
        return 0;
      }
      if (value > highestUpperBound) {
        return numBuckets - 1;
      }
      BigDecimal relativized = BigDecimal.valueOf(value - lowestUpperBound);
      return relativized.divide(bucketWidth, RoundingMode.CEILING).intValue();
    };

    Function<Integer, Long> bucketIndexToUpperBoundFunction =
        (index) -> lowestUpperBound + bucketWidth.multiply(BigDecimal.valueOf(index)).longValue();

    return new FunctionBasedBucketSelector<>(valueToBucketIndexFunction,
        bucketIndexToUpperBoundFunction, numBuckets);
  }
}
