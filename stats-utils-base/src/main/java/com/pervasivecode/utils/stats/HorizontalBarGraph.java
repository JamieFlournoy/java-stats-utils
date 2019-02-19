package com.pervasivecode.utils.stats;

import static com.google.common.base.Preconditions.checkState;
import static java.math.RoundingMode.HALF_EVEN;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * A textual representation of a set of values, in the form of a bar graph growing rightward from
 * an axis on left.
 * <p>
 * Each line of the graph consists of a label, a space, a bar, space-padding, and a formatted
 * magnitude.
 * <p>
 * Example: A graph of width 20 with labels {"Ducks", "Goats", "Cows"}, magnitudes {3, 6, 8}, and
 * formatted magnitudes {"three", "eight", "six"} would be formatted like this:
 *
 * <pre>
 * Ducks ***      three
 * Goats ******** eight
 * Cows  ******   six
 * </pre>
 *
 */
@Immutable
@AutoValue
public abstract class HorizontalBarGraph {
  protected HorizontalBarGraph() {}

  static class FormattingHints {
    int maxBarWidth = -1;
    int maxFormattedMagnitudeLength = -1;
    int maxLabelWidth = -1;
    long maxMagnitude = -1;
  }

  protected abstract int width();

  protected abstract int numRows();

  protected abstract char barPart();

  protected abstract List<String> labels();

  protected abstract List<Long> magnitudes();

  protected abstract List<String> formattedMagnitudes();

  protected abstract FormattingHints formattingHints();

  /**
   * Build the String representation of this bar graph and return it.
   * @return The bar graph.
   */
  public String format() {
    final int numRows = numRows();
    final int maxBarWidth = formattingHints().maxBarWidth;
    final int maxFormattedMagnitudeLength = formattingHints().maxFormattedMagnitudeLength;
    final int maxLabelWidth = formattingHints().maxLabelWidth;
    final long maxMagnitude = formattingHints().maxMagnitude;

    final boolean showBar = (maxBarWidth > 0) && maxMagnitude > 0;
    final double barWidthPerUnitMagnitude = showBar ? (((double) maxBarWidth) / maxMagnitude) : 0.0;
    final char barPart = barPart();

    final String[] labels = labels().toArray(new String[0]);
    final Long[] magnitudes = magnitudes().toArray(new Long[0]);
    final String[] formattedMagnitudes = formattedMagnitudes().toArray(new String[0]);

    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < numRows; i++) {
      sb.append(Strings.padEnd(labels[i], maxLabelWidth, ' '));
      sb.append(' ');
      if (showBar) {
        final double numStars = magnitudes[i] * barWidthPerUnitMagnitude;
        final int numWholeStars = new BigDecimal(numStars).setScale(0, HALF_EVEN).intValueExact();
        for (int b = 0; b < maxBarWidth; b++) {
          sb.append(b < numWholeStars ? barPart : ' ');
        }
        sb.append(' ');
      }
      sb.append(Strings.padStart(formattedMagnitudes[i], maxFormattedMagnitudeLength, ' '));
      sb.append('\n');
    }
    return sb.toString();
  }

  /**
   * Obtain a builder that allows construction of a new instance.
   * <p>
   * The following properties are set to default values:
   * <ul>
   * <li>barPart = '*'
   * </ul>
   *
   * @return A new, mostly-empty Builder instance.
   */
  public static HorizontalBarGraph.Builder builder() {
    return new AutoValue_HorizontalBarGraph.Builder().setBarPart('*')
        .setFormattingHints(new FormattingHints());
  }

  /**
   * Obtain a builder that allows construction of a new instance, prepopulated with values from
   * another instance.
   *
   * @param initialValues The instance whose values should be used to populate the new Builder
   *        instance.
   * @return a new Builder instance with the same values as the {@code initialValues} parameter.
   */
  public static HorizontalBarGraph.Builder builder(HorizontalBarGraph initialValues) {
    return builder().setBarPart(initialValues.barPart()) //
        .setFormattedMagnitudes(initialValues.formattedMagnitudes()) //
        .setFormattingHints(initialValues.formattingHints()) //
        .setLabels(initialValues.labels()) //
        .setMagnitudes(initialValues.magnitudes()) //
        .setNumRows(initialValues.numRows()) //
        .setWidth(initialValues.width());
  }

  /**
   * An object that can be used to create a {@link HorizontalBarGraph}.
   */
  @AutoValue.Builder
  public static abstract class Builder {
    protected Builder() {}

    /**
     * Set the character width of the graph.
     *
     * @param width The desired width of the graph in characters.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    public abstract Builder setWidth(int width);

    /**
     * Set the number of data rows in the graph. This specifies the required number of elements in
     * the labels, magnitudes, and formattedMagnitudes lists that are provided to this builder.
     *
     * @param numRows The number of data rows.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    public abstract Builder setNumRows(int numRows);

    /**
     * Set the character used to render the bar (which is a variable-width representation of each
     * row's magnitude value).
     *
     * @param barPart The character to use to render the bar.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    public abstract Builder setBarPart(char barPart);

    /**
     * Set the labels used to identify which value each row represents.
     *
     * @param labels The labels that identify what value each row represents.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    public abstract Builder setLabels(List<String> labels);

    /**
     * Set the magnitudes of the values shown as bars of varying lengths. The largest magnitude
     * from this list will be shown as a full-width bar, and all other magnitudes are shown as bars
     * with shorter bars, in linear proportion to that largest value.
     *
     * @param magnitudes The values that control the width of the bars.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    public abstract Builder setMagnitudes(List<Long> magnitudes);

    /**
     * Set the formatted version of the magnitude, which is shown to the right of the bar.
     *
     * @param formattedMagnitudes The formatted representation of the magnitude value.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    public abstract Builder setFormattedMagnitudes(List<String> formattedMagnitudes);

    /**
     * Derived values that are used in formatting, which are also used by validation code. This is
     * a field in order to avoid code duplication, so that the values can be computed once and then
     * used by validation and again by formatting logic.
     *
     * @param hints The derived values.
     * @return A builder that can be used to finish creating a {@link HorizontalBarGraph} instance.
     */
    protected abstract Builder setFormattingHints(FormattingHints hints);

    protected abstract HorizontalBarGraph buildInternal();

    /**
     * Validate the contents of this Builder and create a {@link HorizontalBarGraph} instance.
     *
     * @return An immutable HorizontalBarGraph instance that can be used to produce the formatted
     *         representation of the values in this Builder.
     */
    public HorizontalBarGraph build() {
      final HorizontalBarGraph unvalidated = buildInternal();

      final boolean formattingHintsAreSet = unvalidated.formattingHints().maxLabelWidth > -1;
      if (formattingHintsAreSet) {
        return unvalidated;
      }

      checkState(unvalidated.labels().size() == unvalidated.numRows(),
          "The size of the list of labels must match numRows.");

      final int maxLabelWidth;
      try {
        maxLabelWidth = maxLength(unvalidated.labels());
      } catch (NullPointerException npe) {
        throw new IllegalStateException("labels cannot contain null values.", npe);
      }

      checkState(unvalidated.magnitudes().size() == unvalidated.numRows(),
          "The size of the list of magnitudes must match numRows.");

      final int width = unvalidated.width();
      final int maxFormattedMagnitudeLength = maxLength(unvalidated.formattedMagnitudes());
      final int minFormattableWidth = (maxLabelWidth + 1 + maxFormattedMagnitudeLength);

      checkState(width >= minFormattableWidth,
          "Width value (%s chars) is too small to fit contents (%s chars wide without bar graph).",
          width, minFormattableWidth);

      final FormattingHints hints = new FormattingHints();
      hints.maxBarWidth = width - (maxLabelWidth + maxFormattedMagnitudeLength + 2);
      hints.maxFormattedMagnitudeLength = maxFormattedMagnitudeLength;
      hints.maxLabelWidth = maxLabelWidth;
      try {
        hints.maxMagnitude = max(unvalidated.magnitudes());
      } catch (NullPointerException npe) {
        throw new IllegalStateException("magnitudes cannot contain null values.", npe);
      }

      // Return an instance that uses lists that are definitely immutable, with the formatting hints
      // we have calculated in order to do validation.
      return HorizontalBarGraph.builder(unvalidated) //
          .setFormattedMagnitudes(ImmutableList.copyOf(unvalidated.formattedMagnitudes())) //
          .setLabels(ImmutableList.copyOf(unvalidated.labels())) //
          .setMagnitudes(ImmutableList.copyOf(unvalidated.magnitudes())) //
          .setFormattingHints(hints) //
          .build();
    }

    private static int maxLength(List<String> strings) {
      int max = 0;
      for (String s : strings) {
        if (s.length() > max) {
          max = s.length();
        }
      }
      return max;
    }

    private static long max(List<Long> magnitudes) {
      long max = 0L;
      for (Long m : magnitudes) {
        long l = m.longValue();
        checkState(l >= 0, "Magnitude values must be non-negative.");
        if (l > max) {
          max = l;
        }
      }
      return max;
    }
  }
}
