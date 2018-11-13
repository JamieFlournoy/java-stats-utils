package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import com.pervasivecode.utils.stats.histogram.ImmutableHistogram;

public class ImmutableHistogramTest {

  @Test
  public void builder_withNoCounts_shouldThrow() {
    try {
      ImmutableHistogram.<Byte>builder()
          .setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0xff)).build();
      Truth.assert_().fail();
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().isEqualTo("Missing required properties: countByBucket");
    }
  }

  @Test
  public void builder_withEmptyCounts_shouldThrow() {
    try {
      ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
      builder.setCountByBucket(ImmutableList.of());
      builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00));
      builder.build();
      Truth.assert_().fail();
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().isEqualTo("countByBucket cannot be empty");
    }
  }

  @Test
  public void builder_withNoBucketUpperBounds_shouldThrow() {
    try {
      ImmutableHistogram.<Byte>builder().setCountByBucket(ImmutableList.of(42L)).build();
      Truth.assert_().fail();
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().isEqualTo("Missing required properties: bucketUpperBounds");
    }
  }

  @Test
  public void builder_withWrongNumberOfUpperBounds_shouldThrow() {
    try {
      ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
      builder.setCountByBucket(ImmutableList.of(1L, 2L, 3L, 4L, 5L));
      builder
          .setBucketUpperBounds(ImmutableList.of((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
      builder.build();
      Truth.assert_().fail();
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat()
          .isEqualTo("Wrong number of bucketUpperBounds values. (Expected 4, got 5)");
    }

    try {
      ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
      builder.setCountByBucket(ImmutableList.of(1L, 2L, 3L, 4L, 5L));
      builder.setBucketUpperBounds(ImmutableList.of((byte) 1, (byte) 2, (byte) 3));
      builder.build();
      Truth.assert_().fail();
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat()
          .isEqualTo("Wrong number of bucketUpperBounds values. (Expected 4, got 3)");
    }
  }

  @Test
  public void builder_withEmptyBucketUpperBounds_shouldBuild() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.builder();
    builder.setCountByBucket(ImmutableList.of(42L));
    builder.setBucketUpperBounds(ImmutableList.of());
    builder.build();
  }

  private static ImmutableHistogram.Builder<Byte> getHistogramOfByteBuilder() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
    builder.setCountByBucket(ImmutableList.of(42L, 37L, 17L));
    builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0x7f));
    return builder;
  }

  @Test
  public void builder_withAllRequiredProperties_shouldBuild() {
    ImmutableHistogram.Builder<Byte> builder = getHistogramOfByteBuilder();
    builder.build();
  }

  @Test
  public void bucketUpperBound_shouldWork() {
    ImmutableHistogram.Builder<Byte> builder = getHistogramOfByteBuilder();
    ImmutableHistogram<Byte> histogram = builder.build();

    assertThat(histogram.bucketUpperBound(0)).isEqualTo((byte) 0);
    assertThat(histogram.bucketUpperBound(1)).isEqualTo((byte) 0x7f);
  }

  @Test
  public void bucketUpperBound_withLastBucketIndex_shouldThrow() {
    ImmutableHistogram.Builder<Byte> builder = getHistogramOfByteBuilder();
    ImmutableHistogram<Byte> histogram = builder.build();

    try {
      histogram.bucketUpperBound(2);
      Truth.assert_().fail("Expected an IllegalArgumentException.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat()
          .isEqualTo(ImmutableHistogram.NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE);
    }
  }

  @Test
  public void numInBucket_shouldWork() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
    builder.setCountByBucket(ImmutableList.of(42L, 37L, 17L));
    builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0xff));
    ImmutableHistogram<Byte> histogram = builder.build();
    assertThat(histogram.countInBucket(0)).isEqualTo(42L);
    assertThat(histogram.countInBucket(1)).isEqualTo(37L);
    assertThat(histogram.countInBucket(2)).isEqualTo(17L);
  }

  @Test
  public void copyOf_withValidHistogram_shouldWork() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
    builder.setCountByBucket(ImmutableList.of(42L, 37L, 17L));
    builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0xff));
    ImmutableHistogram<Byte> histogram = builder.build();

    ImmutableHistogram<Byte> copy = ImmutableHistogram.copyOf(histogram);
    assertThat(copy).isEqualTo(histogram);
  }
}
