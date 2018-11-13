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

  /**
   * Make a copy of a histogram, transforming its upper bound values into instances of
   * {@link Quantity} with the specified measurement and unit.
   *
   * @param histogram The histogram of plain {@link Number} values.
   * @param baseUnit The unit that the upper bound values in the histogram should represent.
   *        Example: if the histogram contains Long values representing milliseconds, this argument
   *        should represent one millisecond.
   * @param quantityFactory An object that can instantiate objects of type {@code Quantity<Q>}.
   * @param <N> The numeric value type of the input histogram.
   * @param <Q> The Quantity value type of the desired histogram.
   * @return A copy of the input histogram, transformed so its upper bound values are represented as
   *         {@link Quantity} values of measure type Q.
   */
  public static <N extends Number, Q extends Quantity<Q>> ImmutableHistogram<Quantity<Q>> of(
      Histogram<N> histogram, Unit<Q> baseUnit, QuantityFactory<Q> quantityFactory) {
    return ImmutableHistogram.copyOf(
        Histograms.transformValues(histogram, (ub) -> quantityFactory.create(ub, baseUnit)));
  }
}
