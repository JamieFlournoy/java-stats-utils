package com.pervasivecode.utils.stats.histogram;

import java.text.NumberFormat;
import java.util.function.Function;
import com.google.auto.value.AutoValue;

/**
 * Configuration for a {@link HistogramFormatter}.
 *
 * @param <T> The type of value that this HistogramFormat can handle.
 */
@AutoValue
public abstract class HistogramFormat<T> {


  /**
   * A function that formats a bucket upper bound value as a String.
   *
   * @return The value formatter function.
   */
  public abstract Function<T, String> upperBoundValueFormatter();

  /**
   * The string label to use when the Histogram contains exactly one bucket.
   *
   * @return
   */
  public abstract String labelForSingularBucket();

  /**
   * A NumberFormat that formats the percentage value representing the part of the total count that
   * is represented by the current bucket.
   *
   * @return The NumberFormat to use when formatting percent values.
   */
  public abstract NumberFormat percentFormat();

  /**
   * The maximum width of the bar graph portion of the formatted representation, in characters.
   *
   * @return The maximum width of the bar graph.
   */
  public abstract int maxBarGraphWidth();
  // TODO add a way to specify the width of the whole thing
  // TODO add a policy specification about how to handle situations where max width requires
  // dropping columns (OK to not show bar graph? OK to not show %?)
  // TODO generalize % into a Function that decides if percent or %+count or whatever should be
  // shown.

  public static <T> HistogramFormat.Builder<T> builder() {
    return new AutoValue_HistogramFormat.Builder<T>().setLabelForSingularBucket("?");
  }

  @AutoValue.Builder
  public static abstract class Builder<T> {
    public abstract HistogramFormat.Builder<T> setUpperBoundValueFormatter(
        Function<T, String> upperBoundValueFormatter);

    public abstract HistogramFormat.Builder<T> setLabelForSingularBucket(
        String labelForSingularBucket);

    public abstract HistogramFormat.Builder<T> setPercentFormat(NumberFormat percentFormat);

    public abstract HistogramFormat.Builder<T> setMaxBarGraphWidth(int maxBarGraphWidth);

    protected abstract HistogramFormat<T> buildInternal();

    public HistogramFormat<T> build() {
      HistogramFormat<T> format = buildInternal();

      // TODO verify that maxBarGraphWidth >=0; verify that maxBarGraphWidth=0 works

      return format;
    }
  }
}
