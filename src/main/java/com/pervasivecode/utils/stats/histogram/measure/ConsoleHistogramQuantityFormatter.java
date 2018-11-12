package com.pervasivecode.utils.stats.histogram.measure;

import javax.measure.Quantity;
import com.pervasivecode.utils.measure.api.QuantityFormatter;
import com.pervasivecode.utils.stats.histogram.ConsoleHistogramFormatter;
import com.pervasivecode.utils.stats.histogram.Histogram;

/**
 * Format {@link Histogram} contents for a text display. The formatted output consists of a vertical
 * list of labeled histogram buckets, with horizontal bar graphs showing the count in each bucket,
 * followed by a percentage that represents the number of items counted in this bucket as a fraction
 * of all items counted by this histogram.
 * <p>
 * This version of {@link ConsoleHistogramFormatter} handles JSR-363 {@link Quantity} objects, which
 * represent values of a certain measurement (e.g. {@link javax.measure.quantity.Mass Mass},
 * {@link javax.measure.quantity.Energy Energy}, {@link javax.measure.quantity.Length Length},
 * {@link javax.measure.quantity.LuminousFlux LuminousFlux}) that have a unit attached (e.g.
 * kilometers, megajoules, furlongs, bits per second, etc.).
 * <p>
 * Example output from a
 * {@code ConsoleHistogramQuantityFormatter<}{@link javax.measure.quantity.Power Power}{@code >}:
 *
 * <pre>
 * &lt;= 1 kW                                                                  0%
 * &lt;= 10 kW                                                                 0%
 * &lt;= 100 kW                                                                0%
 * &lt;= 1 MW   ****                                                           2%
 * &lt;= 10 MW  ************************************************************  38%
 * &lt;= 100 MW ********************************************************      35%
 * &lt;= 1 GW   ******************************                                19%
 * &lt;= 10 GW  ********                                                       5%
 * &gt;  10 GW                                                                 0%
 * </pre>
 *
 * @param <T> The type of quantity value the histogram counted, which this formatter will format
 *        using the provided {@code quantityFormatter}.
 */
public class ConsoleHistogramQuantityFormatter<T extends Quantity<T>>
    extends ConsoleHistogramFormatter<Quantity<T>> {
  /**
   * Create a ConsoleHistogramQuantityFormatter that formats upper bound values using
   * {@code quantityFormatter}.
   * 
   * @param quantityFormatter An object that converts an upper bound quantity into a String
   *        representation.
   * @param maxBarGraphWidth The maximum width of the bar graph portion of the formatted
   *        representation.
   */
  public ConsoleHistogramQuantityFormatter(QuantityFormatter<T> quantityFormatter,
      int maxBarGraphWidth) {
    super((v) -> quantityFormatter.format(v), maxBarGraphWidth);
  }
}
