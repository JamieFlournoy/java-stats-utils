package com.pervasivecode.utils.stats.histogram;

import java.text.NumberFormat;
import java.util.function.Function;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class HistogramFormat<T> {

  public abstract Function<T, String> upperBoundValueFormatter();

  public abstract String labelForSingularBucket();

  public abstract NumberFormat percentFormat();

  public abstract int maxBarGraphWidth();
  // TODO add a config field that is itself an autovalue object, with a maxFormattedWidth option.
  // let the formatter figure out the maxBarGraphWidth if that option is given, allowing a
  // zero-maxBarGraphWidth and adding a config option for what to do if the maxFormattedWidth is
  // impossible. (throw, silently exceed the maximum, or drop the % column)
  
  public static <T> HistogramFormat.Builder<T> builder() {
    return new AutoValue_HistogramFormat.Builder<T>()
        .setLabelForSingularBucket("?");
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

      // TODO maxBarGraphWidth >=0
      // checkArgument(format.maxBarGraphWidth() > 1, "maxWidth must be at least 2.");

      // TODO add hideBarGraph

      return format;
    }
  }
}
