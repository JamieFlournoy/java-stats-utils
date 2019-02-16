package com.pervasivecode.utils.stats;

import static com.google.common.truth.Truth.assertThat;
import java.util.ArrayList;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;

public class HorizontalBarGraphTest {

  private HorizontalBarGraph.Builder validBuilder() {
    return HorizontalBarGraph.builder().setBarPart('#') //
        .setFormattedMagnitudes(ImmutableList.of("x", "y", "z")) //
        .setLabels(ImmutableList.of("A ->", "B ->", "C -->")) //
        .setMagnitudes(ImmutableList.of(123L, 45L, 6L)) //
        .setNumRows(3) //
        .setWidth(20);
  }

  @Test
  public void build_withZeroRows_shouldThrow() {
    try {
      validBuilder().setNumRows(0).build();
      Truth.assert_().fail("Expected an exception due to the numRows value of 0.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("numRows");
    }
  }

  @Test
  public void build_withZeroWidth_shouldThrow() {
    try {
      validBuilder().setWidth(0).build();
      Truth.assert_().fail("Expected an exception due to the zero width value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("Width");
    }
  }

  @Test
  public void build_withImpossibleWidth_shouldThrow() {
    try {
      validBuilder().setWidth(5).build();
      Truth.assert_().fail("Expected an exception due to the width being too narrow.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("Width");
    }
  }

  @Test
  public void build_withWidthSoSmallGraphIsZeroWidth_shouldWork() {
    // Widest row is "C --> z"
    HorizontalBarGraph graph = validBuilder().setWidth(7).build();
    assertThat(graph.format()).isEqualTo("" //
        + "A ->  x\n" //
        + "B ->  y\n" //
        + "C --> z\n");
  }

  @Test
  public void build_withNullLabels_shouldThrow() {
    ArrayList<String> labels = new ArrayList<>();
    labels.add("label1");
    labels.add(null);
    labels.add("label3");
    try {
      validBuilder().setLabels(labels).build();
      Truth.assert_().fail("Expected an exception due to the presence of a null label value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("labels");
    }
  }

  @Test
  public void build_withWrongNumberOfLabels_shouldThrow() {
    try {
      validBuilder().setLabels(ImmutableList.of("label1", "label2")).build();
      Truth.assert_().fail("Expected an exception due to the missing label value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("labels");
    }
  }

  @Test
  public void build_withNullMagnitudes_shouldThrow() {
    ArrayList<Long> magnitudes = new ArrayList<Long>();
    magnitudes.add(47L);
    magnitudes.add(null);
    magnitudes.add(901L);
    try {
      validBuilder().setMagnitudes(magnitudes).build();
      Truth.assert_().fail("Expected an exception due to the presence of a null magnitude value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("magnitudes");
    }
  }

  @Test
  public void build_withWrongNumberOfMagnitudes_shouldThrow() {
    try {
      validBuilder().setMagnitudes(ImmutableList.of(47L, 901L)).build();
      Truth.assert_().fail("Expected an exception due to the missing magnitude value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("magnitudes");
    }
  }

  @Test
  public void build_withAnyNegativeMagnitudes_shouldThrow() {
    try {
      validBuilder().setMagnitudes(ImmutableList.of(47L, -31L, 901L)).build();
      Truth.assert_().fail("Expected an exception due to a negative magnitude value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("negative");
    }
  }

  @Test
  public void format_withRoomForBarPart_shouldWork() {
    HorizontalBarGraph.Builder builder = validBuilder();
    assertThat(builder.build().format()).isEqualTo("" //
        + "A ->  ############ x\n" //
        + "B ->  ####         y\n" //
        + "C --> #            z\n");

    builder.setWidth(10).build();
    assertThat(builder.build().format()).isEqualTo("" //
        + "A ->  ## x\n" //
        + "B ->  #  y\n" //
        + "C -->    z\n");

    builder.setWidth(9).build();
    assertThat(builder.build().format()).isEqualTo("" //
        + "A ->  # x\n" //
        + "B ->    y\n" //
        + "C -->   z\n");
  }

  @Test
  public void format_withNoRoomForBarPart_shouldWork() {
    HorizontalBarGraph.Builder builder = validBuilder();
    String expected = "" //
        + "A ->  x\n" //
        + "B ->  y\n" //
        + "C --> z\n";

    builder.setWidth(8);
    assertThat(builder.build().format()).isEqualTo(expected);

    builder.setWidth(7);
    assertThat(builder.build().format()).isEqualTo(expected);
  }

  @Test
  public void build_withAllZeroMagnitudes_shouldWork() {
    HorizontalBarGraph.Builder builder = validBuilder();
    builder.setMagnitudes(ImmutableList.of(0L, 0L, 0L));
    assertThat(builder.build().format()).isEqualTo("" //
        + "A ->  x\n" //
        + "B ->  y\n" //
        + "C --> z\n");
  }
}
