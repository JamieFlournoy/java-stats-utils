package com.pervasivecode.utils.stats.histogram.measure;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.QuantityFactory;
import com.pervasivecode.utils.stats.histogram.Histogram;
import com.pervasivecode.utils.stats.histogram.Histograms;
import com.pervasivecode.utils.stats.histogram.ImmutableHistogram;

/**
 * This is an adapter to present a histogram of plain numeric types as a histogram whose type is a
 * {@link Quantity}.
 */
public class ImmutableQuantityHistogram {
  public static <N extends Number, Q extends Quantity<Q>> ImmutableHistogram<Quantity<Q>> of(
      Histogram<N> histogram, Unit<Q> baseUnit, QuantityFactory<Q> quantityFactory) {
    return ImmutableHistogram.copyOf(
        Histograms.transformValues(histogram, (ub) -> quantityFactory.create(ub, baseUnit)));
  }
}
