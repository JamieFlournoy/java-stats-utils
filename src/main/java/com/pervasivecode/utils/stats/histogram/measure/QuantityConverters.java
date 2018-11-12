package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.google.common.base.Converter;

class QuantityConverters {

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
