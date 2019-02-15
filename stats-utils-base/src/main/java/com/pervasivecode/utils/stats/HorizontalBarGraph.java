package com.pervasivecode.utils.stats;

import static com.google.common.base.Preconditions.checkState;
import static java.math.RoundingMode.HALF_EVEN;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;

@Immutable
@AutoValue
public abstract class HorizontalBarGraph {
  protected static class FormattingHints {
    int maxBarWidth = -1;
    int maxFormattedMagnitudeLength = -1;
    int maxLabelWidth = -1;
    long maxMagnitude = -1;
  }

  public static HorizontalBarGraph.Builder builder() {
    return new AutoValue_HorizontalBarGraph.Builder().setBarPart('*')
        .setFormattingHints(new FormattingHints());
  }

  public static HorizontalBarGraph.Builder builder(HorizontalBarGraph initialValues) {
    return builder()
        .setBarPart(initialValues.barPart()) //
        .setFormattedMagnitudes(initialValues.formattedMagnitudes()) //
        .setFormattingHints(initialValues.formattingHints()) //
        .setLabels(initialValues.labels()) //
        .setMagnitudes(initialValues.magnitudes()) //
        .setNumRows(initialValues.numRows()) //
        .setWidth(initialValues.width());
  }
  
  protected abstract int width();

  protected abstract int numRows();

  protected abstract char barPart();

  protected abstract List<String> labels();

  protected abstract List<Long> magnitudes();

  protected abstract List<String> formattedMagnitudes();

  protected abstract FormattingHints formattingHints();

  @Override
  public final String toString() {
    final String labelFormat = "%-" + formattingHints().maxLabelWidth + "s";
    final String percentFormat = "%" + formattingHints().maxFormattedMagnitudeLength + "s";
    final int numRows = numRows();

    final int maxBarWidth = formattingHints().maxBarWidth;
    final long maxMagnitude = formattingHints().maxMagnitude;

    final boolean showBar = (maxBarWidth > 0);
    final double barWidthPerUnitMagnitude = showBar ? (((double) maxBarWidth) / maxMagnitude) : 0.0;
    final char barPart = barPart();

    final String[] labels = labels().toArray(new String[0]);
    final Long[] magnitudes = magnitudes().toArray(new Long[0]);

    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < numRows; i++) {
      sb.append(String.format(labelFormat, labels[i]));
      sb.append(' ');
      if (showBar) {
        final double numStars = magnitudes[i] * barWidthPerUnitMagnitude;
        final int numWholeStars = new BigDecimal(numStars).setScale(0, HALF_EVEN).intValueExact();
        for (int b = 0; b < maxBarWidth; b++) {
          sb.append(b < numWholeStars ? barPart : ' ');
        }
        sb.append(' ');
      }
      sb.append(String.format(percentFormat, formattedMagnitudes().get(i)));
      sb.append('\n');
    }
    return sb.toString();
  }

  @AutoValue.Builder
  public static abstract class Builder {
    public abstract Builder setWidth(int width);

    public abstract Builder setNumRows(int numRows);

    public abstract Builder setBarPart(char barPart);

    public abstract Builder setLabels(List<String> labels);

    public abstract Builder setMagnitudes(List<Long> magnitudes);

    public abstract Builder setFormattedMagnitudes(List<String> formattedMagnitudes);

    protected abstract Builder setFormattingHints(FormattingHints hints);

    protected abstract HorizontalBarGraph buildInternal();

    public HorizontalBarGraph build() {
      HorizontalBarGraph unvalidated = buildInternal();

      if (unvalidated.formattingHints().maxLabelWidth >= 0) {
        return unvalidated;
      }
      
      int maxLabelWidth = maxLength(unvalidated.labels());
      int maxFormattedMagnitudeLength = maxLength(unvalidated.formattedMagnitudes());
      int maxBarWidth = unvalidated.width() - (maxLabelWidth + maxFormattedMagnitudeLength + 2);

      int minFormattableWidth = (maxLabelWidth + 1 + maxFormattedMagnitudeLength);
      checkState(unvalidated.width() >= minFormattableWidth,
          "Width value (%s chars) is too small to fit contents (%s chars wide without bar graph).",
          unvalidated.width(), minFormattableWidth);
      
      FormattingHints hints = new FormattingHints();
      hints.maxBarWidth = maxBarWidth;
      hints.maxFormattedMagnitudeLength = maxFormattedMagnitudeLength;
      hints.maxLabelWidth = maxLabelWidth;
      hints.maxMagnitude = max(unvalidated.magnitudes());

      // TODO
//      checkState(hints.maxFormattedMagnitudeLength > 0,
//          "formattedMagnitudes must contain at least one nonempty string.");
//      checkState(hints.maxLabelWidth > 0, "labels must contain at least one nonempty string.");
//      checkState(hints.maxMagnitude > 0, "magnitudes must contain at least one positive value");

      return HorizontalBarGraph.builder(unvalidated).setFormattingHints(hints).build();
    }
  }

  private static int maxLength(List<String> strings) {
    int max = 0;
    for (String s : strings) {
      if (s != null && s.length() > max) {
        max = s.length();
      }
    }
    return max;
  }

  private static long max(List<Long> magnitudes) {
    long max = 0L;
    for (Long l : magnitudes) {
      if (l != null && l.longValue() > max) {
        max = l.longValue();
      }
    }
    return max;
  }
}
