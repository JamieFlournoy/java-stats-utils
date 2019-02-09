package com.pervasivecode.utils.stats.histogram.measure;

import static com.pervasivecode.utils.stats.histogram.BucketSelectors.linearLongValues;
import static com.pervasivecode.utils.stats.histogram.BucketSelectors.powerOf2LongValues;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.google.common.base.Converter;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.BucketSelectors;
import com.pervasivecode.utils.stats.histogram.BucketingSystem;

/**
 * BucketSelector factory methods for basic bucketing strategies, working with values that are
 * instances of {@link javax.measure.Quantity}{@code <T>}.
 *
 * @see BucketingSystem
 * @see BucketSelectors
 */
public class QuantityBucketSelectors {
  private QuantityBucketSelectors() {}

  /**
   * Get a BucketSelector that has upper bound values that are {@code Quantity<T>} instances whose
   * numeric component is part of an exponential series made up of consecutive whole-number powers
   * of 2.
   * <p>
   * Example: arguments {@code minPower=2}, {@code baseUnit=Sievert}, {@code numBuckets=5} would
   * generate upper bound values of 4 Sv, 8 Sv, 16 Sv, and 32 Sv.
   *
   * @param minPower The smallest power of 2 to use when generating upper bound values.
   * @param baseUnit The base "ones" unit of measurement type T in the desired system of units.
   *        Example: kilogram, meter, square foot.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @param numBuckets The number of buckets.
   * @param <T> The kind of quantity that the BucketSelector will bucket. Example:
   *        {@link javax.measure.quantity.Temperature Temperature}.
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
   * numeric component is part of an exponential series. Each bucket value's exponent is 1.0 plus
   * the exponent of the previous bucket value.
   * <p>
   * Example: arguments {@code base=5.0}, {@code baseUnit=Acre}, {@code minPower=0.0}, and
   * {@code numBuckets=5} would generate base and exponent values of
   *
   * 5<sup>0</sup> acres, 5<sup>1</sup> acres, 5<sup>2</sup> acres, and 5<sup>3</sup> acres, so the
   * upper bound values would be 1 acre, 5 acres, 25 acres, and 125 acres.
   *
   * @param base The base that will be raised to the specified exponents to generate upper bound
   *        values.
   * @param baseUnit The base "ones" unit of measurement type T in the desired system of units.
   *        Example: kilogram, meter, square foot.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @param minPower The smallest exponent to use when generating upper bound values.
   * @param numBuckets The number of buckets into which values should be counted.
   * @param <T> The kind of quantity that the BucketSelector will bucket. Example:
   *        {@link javax.measure.quantity.Temperature Temperature}.
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
   * arguments {@code baseUnit=kilogram}, {@code lowestUpperBound=0}, {@code highestUpperBound=20},
   * and {@code numBuckets=6} would generate upper bound values 0, 5, 10, 15, and 20. Example: 0 kg,
   * 5 kg, 10 kg, 15 kg, 20 kg.
   *
   * @param baseUnit The base "ones" unit of measurement type T in the desired system of units.
   *        Example: kilogram, meter, square foot.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @param lowestUpperBound The upper-bound value for the first bucket (index 0).
   * @param highestUpperBound The upper-bound value for the next-to-last bucket
   *        ({@code index (numBuckets - 2)}).
   * @param numBuckets The total number of buckets.
   * @param <T> The kind of quantity that the BucketSelector will bucket. Example:
   *        {@link javax.measure.quantity.Temperature Temperature}.
   * @return A {@code BucketSelector<Long>} instance.
   */
  public static <T extends Quantity<T>> BucketSelector<Quantity<T>> linear(Unit<T> baseUnit,
      QuantityFactory<T> quantityFactory, Quantity<T> lowestUpperBound,
      Quantity<T> highestUpperBound, int numBuckets) {
    Converter<Quantity<T>, Long> transformer =
        QuantityConverters.quantityToLongTransformer(baseUnit, quantityFactory);

    long lowestUpperBoundQty = transformer.convert(lowestUpperBound);
    long highestUpperBoundQty = transformer.convert(highestUpperBound);
    BucketSelector<Long> unitlessBucketer =
        linearLongValues(lowestUpperBoundQty, highestUpperBoundQty, numBuckets);

    return BucketSelectors.transform(unitlessBucketer, transformer);
  }
}
