package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.pervasivecode.utils.stats.histogram.BucketSelector;

public class QuantityBucketSelectors {
  public static class ExponentialQuantityBucketSelector<Q extends Quantity<Q>>
      implements BucketSelector<Quantity<Q>> {
    private final double base;
    private final Unit<Q> baseUnit;
    private final QuantityFactory<Q> quantityFactory;
    private final double minPower;
    private final int numBuckets;

    public ExponentialQuantityBucketSelector( //
        double base, //
        Unit<Q> baseUnit, //
        QuantityFactory<Q> quantityFactory, //
        double minPower, //
        int numBuckets) {
      checkArgument(base > 0, "base must be greater than 1.");
      checkArgument(minPower >= 0, "minPower must be non-negative.");
      checkArgument(numBuckets > 0, "numBuckets must be greater than 0.");
      this.base = base;
      this.baseUnit = checkNotNull(baseUnit);
      this.quantityFactory = checkNotNull(quantityFactory);
      this.minPower = minPower;
      this.numBuckets = numBuckets;
    }

    @Override
    public int numBuckets() {
      return this.numBuckets;
    }

    @Override
    public Quantity<Q> bucketUpperBound(int index) {
      return quantityFactory.create(Math.pow(base, minPower + index), baseUnit);
    }

    @Override
    public int bucketIndexFor(Quantity<Q> value) {
      double valueAsDoubleInBaseUnits = value.to(baseUnit).getValue().doubleValue();
      double logOfValue = Math.log(valueAsDoubleInBaseUnits);
      double logOfBase = Math.log(base);
      double indexAsDouble = (logOfValue / logOfBase) - minPower;
      int index = Math.min((int) Math.ceil(indexAsDouble), numBuckets - 1);
      return index;
    }
  }

  public static <T extends Quantity<T>> BucketSelector<Quantity<T>> exponential(double base, //
      Unit<T> baseUnit, //
      QuantityFactory<T> quantityFactory, //
      double minPower, //
      int numBuckets) {
    return new ExponentialQuantityBucketSelector<T>(base, baseUnit, quantityFactory, minPower,
        numBuckets);
  }
}
