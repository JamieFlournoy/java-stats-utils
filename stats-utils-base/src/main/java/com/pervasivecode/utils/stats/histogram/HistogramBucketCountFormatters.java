package com.pervasivecode.utils.stats.histogram;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.BiFunction;

/**
 * Factory methods for use with {@link HistogramFormat.Builder#setBucketCountFormatter(BiFunction)}.
 */
public class HistogramBucketCountFormatters {
  private HistogramBucketCountFormatters() {}

  /**
   * Make a BiFunction that can be used to format histogram bucket count values as a percentage of
   * the total number counted by the histogram.
   * <p>
   * If you don't need to configure the default percentage formatter, you can use the
   * {@link #percentFormatter(Locale)} version of this method instead.
   * 
   * @param percentFormat The localized formatter that will format a number between 0.0 and 1.0 as a
   *        percentage.
   * @return A BiFunction wrapping the provided percentFormat.
   */
  public static BiFunction<Long, Long, String> percentFormatter(NumberFormat percentFormat) {
    return (mag, total) -> percentFormat.format((double) mag / total);
  }

  /**
   * Make a BiFunction that can be used to format histogram bucket count values as a percentage of
   * the total number counted by the histogram.
   * <p>
   * This method obtains the default percentage formatter for a {@link Locale}. If you need to
   * configure the percentage formatter, you can use the {@link #percentFormatter(NumberFormat)}
   * version of this method instead.
   *
   * @param Locale The locale to use when obtaining a default percentage {@link NumberFormat}.
   * @return A BiFunction wrapping the default percent format for the specified Locale.
   */
  public static BiFunction<Long, Long, String> percentFormatter(Locale formatLocale) {
    return percentFormatter(NumberFormat.getPercentInstance(formatLocale));
  }
}
