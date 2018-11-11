package com.pervasivecode.utils.stats.histogram.measure;

import javax.measure.Quantity;
import com.pervasivecode.utils.measure.api.QuantityFormatter;
import com.pervasivecode.utils.stats.histogram.ConsoleHistogramFormatter;

/**
 * Render a vertical list of histogram buckets with horizontal bar graphs showing the count in each
 * bucket.
 * <p>
 * This version of {@link ConsoleHistogramFormatter} handles JSR-363 {@link Quantity} objects, which
 * represent values that have a unit attached (e.g. kilometers, megajoules, furlongs, bits per
 * second, etc.).
 *
 * @param <T> The type of quantity value the histogram counted, which this formatter will format
 *        using the provided {@code quantityFormatter}.
 */
public class ConsoleHistogramQuantityFormatter<T extends Quantity<T>>
    extends ConsoleHistogramFormatter<Quantity<T>> {
  public ConsoleHistogramQuantityFormatter(QuantityFormatter<T> quantityFormatter) {
    this(quantityFormatter, 20);
  }

  public ConsoleHistogramQuantityFormatter(QuantityFormatter<T> quantityFormatter, int maxWidth) {
    super((v) -> quantityFormatter.format(v), maxWidth);
  }
}
