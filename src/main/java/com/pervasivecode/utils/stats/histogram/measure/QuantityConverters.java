package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.google.common.base.Converter;

/**
 * Factory methods for Converters needed by {@link QuantityBucketSelectors}.
 */
class QuantityConverters {
  /**
   * Convert {@link Quantity}{@code <T>} values into Long values, and Long values into
   * {@link Quantity}{@code <T>} values.
   *
   * @param baseUnit The base unit for Quantities of measure <T>.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @return A Converter that converts between {@link Quantity}{@code <T>} and Long.
   */
  public static <T extends Quantity<T>> Converter<Quantity<T>, Long> quantityToLongTransformer(
      Unit<T> baseUnit, QuantityFactory<T> quantityFactory) {
    checkNotNull(quantityFactory);
    checkNotNull(baseUnit);

    return new Converter<>() {
      @Override
      protected Long doForward(Quantity<T> value) {
        return value.to(baseUnit).getValue().longValue();
      }

      @Override
      protected Quantity<T> doBackward(Long unitlessValue) {
        return quantityFactory.create(unitlessValue.longValue(), baseUnit);
      }
    };
  }

  /**
   * Convert {@link Quantity}{@code <T>} values into Double values, and Double values into
   * {@link Quantity}{@code <T>} values.
   *
   * @param baseUnit The base unit for Quantities of measure <T>.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<T>}.
   * @return A Converter that converts between {@link Quantity}{@code <T>} and Double.
   */
  public static <T extends Quantity<T>> Converter<Quantity<T>, Double> quantityToDoubleTransformer(
      Unit<T> baseUnit, QuantityFactory<T> quantityFactory) {
    checkNotNull(baseUnit);
    checkNotNull(quantityFactory);

    return new Converter<>() {
      @Override
      protected Double doForward(Quantity<T> value) {
        return value.to(baseUnit).getValue().doubleValue();
      }

      @Override
      protected Quantity<T> doBackward(Double unitlessValue) {
        return quantityFactory.create(unitlessValue.doubleValue(), baseUnit);
      }
    };
  }
}
