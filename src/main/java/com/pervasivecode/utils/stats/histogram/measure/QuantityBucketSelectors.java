package com.pervasivecode.utils.stats.histogram.measure;

import static java.math.RoundingMode.CEILING;
import static java.math.RoundingMode.HALF_EVEN;
import java.math.BigDecimal;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.google.common.base.Converter;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.ConverterBasedBucketSelector;

public class QuantityBucketSelectors {
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
    Converter<Quantity<T>, Integer> converter = new Converter<>() {
      @Override
      protected Integer doForward(Quantity<T> value) {
        double valueAsDoubleInBaseUnits = value.to(baseUnit).getValue().doubleValue();
        double logOfValue = Math.log(valueAsDoubleInBaseUnits);
        double logOfBase = Math.log(base);

        // Use BigDecimal and round carefully, to work around Double precision limitations.
        // Example: Math.log(125)/Math.log(5) => 3.0000000000000004 (should be exactly 3).
        BigDecimal indexAsBig = BigDecimal.valueOf(logOfValue) //
            .divide(BigDecimal.valueOf(logOfBase), 12, HALF_EVEN) //
            .subtract(BigDecimal.valueOf(minPower));

        int indexIgnoringNumBuckets = indexAsBig.setScale(0, CEILING).intValue();
        return Math.min(indexIgnoringNumBuckets, numBuckets - 1);
      }

      @Override
      protected Quantity<T> doBackward(Integer index) {
        return quantityFactory.create(Math.pow(base, minPower + index), baseUnit);
      }
    };

    return new ConverterBasedBucketSelector<>(converter, numBuckets);
  }
}
