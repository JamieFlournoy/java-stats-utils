package com.pervasivecode.utils.stats.histogram.measure;

import static com.pervasivecode.utils.stats.histogram.BucketSelectors.linearLongValues;
import static com.pervasivecode.utils.stats.histogram.BucketSelectors.powerOf2LongValues;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.google.common.base.Converter;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.BucketSelectors;

public class QuantityBucketSelectors {
  /**
   * Get a BucketSelector that has upper bound values that are {@code Quantity<T>} instances whose
   * numeric component is part of an exponential series made up of consecutive whole-number powers
   * of 2.
   * <p>
   * Example: upper bound values 4 Sv, 8 Sv, 16 Sv, 32 Sv.
   *
   * @param minPower The smallest power of 2 to use when generating upper bound values.
   * @param baseUnit The base "ones" unit of measurement type T in the desired system of units.
   *        Example: kilogram, meter, square foot.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @param numBuckets The number of buckets.
   * @return A {@code BucketSelector<Long>} instance.
   */
  public static <T extends Quantity<T>> BucketSelector<Quantity<T>> powerOf2(int minPower,
      Unit<T> baseUnit, QuantityFactory<T> quantityFactory, final int numBuckets) {
    Converter<Quantity<T>, Long> valueTransformer =
        QuantityConverters.quantityToLongTransformer(baseUnit, quantityFactory);
    BucketSelector<Long> unitlessBucketer = powerOf2LongValues(minPower, numBuckets);
    return BucketSelectors.transform(unitlessBucketer, valueTransformer);
  }

  /**
   * Get a BucketSelector that has upper bound values that are {@code Quantity<T>} instances whose
   * numeric component is part of an exponential series.
   * <p>
   * Example: upper bound values 1 acre, 5 acres, 25 acres, 125 acres.
   *
   * @param base The base that will be raised to the specified exponents to generate upper bound
   *        values.
   * @param baseUnit The base "ones" unit of measurement type T in the desired system of units.
   *        Example: kilogram, meter, square foot.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @param minPower The smallest exponent to use when generating upper bound values.
   * @param numBuckets The number of buckets into which values should be counted.
   * @param <T> The type of measurement described by the bucket upper bounds. Example: Length.
   *
   * @return A {@code BucketSelector<Quantity<T>>} instance.
   */
  public static <T extends Quantity<T>> BucketSelector<Quantity<T>> exponential(double base,
      Unit<T> baseUnit, QuantityFactory<T> quantityFactory, double minPower, int numBuckets) {
    Converter<Quantity<T>, Double> transformer =
        QuantityConverters.quantityToDoubleTransformer(baseUnit, quantityFactory);
    BucketSelector<Double> unitlessBucketer =
        BucketSelectors.exponential(base, minPower, numBuckets);
    return BucketSelectors.transform(unitlessBucketer, transformer);
  }


  /**
   * Get a BucketSelector that has upper bound values that are evenly distributed between a smallest
   * and largest value.
   * <p>
   * Example: 0 kg, 5 kg, 10 kg, 15 kg, 20 kg.
   *
   * @param lowestUpperBound The upper-bound value for the first bucket (index 0).
   * @param highestUpperBound The upper-bound value for the next-to-last bucket
   *        ({@code index (numBuckets - 2)}).
   * @param numBuckets The total number of buckets.
   * @return A {@code BucketSelector<Long>} instance.
   */
  public static <T extends Quantity<T>> BucketSelector<Quantity<T>> linear(Unit<T> baseUnit,
      QuantityFactory<T> quantityFactory, Quantity<T> lowestUpperBound,
      Quantity<T> highestUpperBound, int numBuckets) {
    Converter<Quantity<T>, Long> transformer = QuantityConverters.quantityToLongTransformer(baseUnit, quantityFactory);

    long lowestUpperBoundQty = transformer.convert(lowestUpperBound);
    long highestUpperBoundQty = transformer.convert(highestUpperBound);
    BucketSelector<Long> unitlessBucketer =
        linearLongValues(lowestUpperBoundQty, highestUpperBoundQty, numBuckets);

    return BucketSelectors.transform(unitlessBucketer, transformer);
  }
}
