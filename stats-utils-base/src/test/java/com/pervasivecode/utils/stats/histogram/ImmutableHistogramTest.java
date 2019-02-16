package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.truth.Truth;

public class ImmutableHistogramTest {
  private static ImmutableHistogram.Builder<Byte> makeHistogramOfByteBuilder() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
    builder.setCountByBucket(ImmutableList.of(42L, 37L, 17L));
    builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0x7f));
    return builder;
  }

  private static ImmutableHistogram<Integer> makeHistogramOfIntegerPower2Of2() {
    return ImmutableHistogram.<Integer>builder()
        .setBucketUpperBounds(ImmutableList.of(1, 2, 4, 8))
        .setCountByBucket(ImmutableList.of(0L, 10L, 5L, 4L, 6L))
        .build();
  }

  //
  // Tests for builder.
  //
  @Test
  public void builder_withNullExample_shouldThrow() {
    try {
      ImmutableHistogram.builder((ImmutableHistogram<Object>) null);
      Truth.assert_().fail("Expected an exception due to the lack of an input histogram.");
    } catch (NullPointerException npe) {
      assertThat(npe).hasMessageThat().contains("input");
    }
  }

  @Test
  public void builder_withValidExample_shouldCreateEqualInstance() {
    ImmutableHistogram<Integer> original = makeHistogramOfIntegerPower2Of2();
    ImmutableHistogram<Integer> copy = ImmutableHistogram.builder(original).build();
    assertThat(original).isEqualTo(copy);
  }

  //
  // Tests for build() validation.
  //

  @Test
  public void build_withNoCounts_shouldThrow() {
    try {
      ImmutableHistogram.<Byte>builder()
          .setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0xff)).build();
      Truth.assert_().fail("Expected an exception due to the missing list of bucket counts.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().isEqualTo("Missing required properties: countByBucket");
    }
  }

  @Test
  public void build_withEmptyCounts_shouldThrow() {
    try {
      ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
      builder.setCountByBucket(ImmutableList.of());
      builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00));
      builder.build();
      Truth.assert_().fail("Expected an exception due to the empty list of bucket counts.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("countByBucket");
    }
  }

  @Test
  public void build_withNullBucketCountElements_shouldThrow() {
    ImmutableHistogram.Builder<Byte> builder = makeHistogramOfByteBuilder();
    ArrayList<Long> countByBucket = new ArrayList<>();
    countByBucket.add(null);
    countByBucket.add(null);
    countByBucket.add(null);
    builder.setCountByBucket(countByBucket);
    try {
      builder.build();
      Truth.assert_().fail("Expected exception due to a null element of countByBucket");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("null");
      assertThat(ise).hasMessageThat().contains("countByBucket");
    }
  }

  @Test
  public void build_withNoBucketUpperBounds_shouldThrow() {
    try {
      ImmutableHistogram.<Byte>builder().setCountByBucket(ImmutableList.of(42L)).build();
      Truth.assert_().fail("Expected an exception due to the missing list of upper bounds.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().isEqualTo("Missing required properties: bucketUpperBounds");
    }
  }

  @Test
  public void build_withNoBucketsAndNoUpperBounds_shouldThrow() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
    builder.setBucketUpperBounds(ImmutableList.of());
    builder.setCountByBucket(ImmutableList.of());
    try {
      builder.build();
      Truth.assert_().fail("Expected exception due to empty histogram.");
    } catch (IllegalStateException iae) {
      assertThat(iae).hasMessageThat().contains("empty");
    }
  }

  @Test
  public void build_withWrongNumberOfUpperBounds_shouldThrow() {
    try {
      ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
      builder.setCountByBucket(ImmutableList.of(1L, 2L, 3L, 4L, 5L));
      builder
          .setBucketUpperBounds(ImmutableList.of((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
      builder.build();
      Truth.assert_().fail("Expected an exception due to the extra 5th upper-bound value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat()
          .isEqualTo("Wrong number of bucketUpperBounds values. (Expected 4, got 5)");
    }

    try {
      ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
      builder.setCountByBucket(ImmutableList.of(1L, 2L, 3L, 4L, 5L));
      builder.setBucketUpperBounds(ImmutableList.of((byte) 1, (byte) 2, (byte) 3));
      builder.build();
      Truth.assert_().fail("Expected an exception due to the missing 4th upper-bound value.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat()
          .isEqualTo("Wrong number of bucketUpperBounds values. (Expected 4, got 3)");
    }
  }

  @Test
  public void build_singleBucketHistogram_withEmptyBucketUpperBounds_shouldBuild() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.builder();
    builder.setCountByBucket(ImmutableList.of(42L));
    builder.setBucketUpperBounds(ImmutableList.of());
    builder.build();
  }

  @Test
  public void build_withNullBucketUpperBound_shouldThrow() {
    ImmutableHistogram.Builder<Byte> builder = makeHistogramOfByteBuilder();
    ArrayList<Byte> upperBoundValues = new ArrayList<>();
    upperBoundValues.add((byte) 0x01);
    upperBoundValues.add(null);
    builder.setBucketUpperBounds(upperBoundValues);
    try {
      builder.build();
      Truth.assert_().fail("Expected exception due to a null element of bucketUpperBounds");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("null");
      assertThat(ise).hasMessageThat().contains("bucketUpperBounds");
    }
  }

  @Test
  public void build_withAllRequiredProperties_shouldBuild() {
    ImmutableHistogram.Builder<Byte> builder = makeHistogramOfByteBuilder();
    builder.build();
  }

  @Test
  public void build_withMutableLists_shouldBuildReallyImmutableInstance() {
    List<Integer> bucketUpperBounds = Lists.newArrayList(1, 2, 4, 8);
    List<Long> countByBucket = Lists.newArrayList(5L, 4L, 3L, 2L, 1L);
    ImmutableHistogram<Integer> histogram = ImmutableHistogram.<Integer>builder() //
        .setBucketUpperBounds(bucketUpperBounds) //
        .setCountByBucket(countByBucket) //
        .build();

    assertThat(histogram.bucketUpperBounds()).isEqualTo(bucketUpperBounds);
    assertThat(histogram.bucketUpperBounds()).isInstanceOf(ImmutableList.class);

    assertThat(histogram.countByBucket()).isEqualTo(countByBucket);
    assertThat(histogram.countByBucket()).isInstanceOf(ImmutableList.class);

    // Verify that just the countByBucket field can be mutable and will still be fixed.
    histogram = ImmutableHistogram.<Integer>builder(histogram) //
        .setCountByBucket(countByBucket) //
        .build();
    assertThat(histogram.countByBucket()).isEqualTo(countByBucket);
    assertThat(histogram.countByBucket()).isInstanceOf(ImmutableList.class);

    // Verify that just the bucketUpperBounds field can be mutable and will still be fixed.
    histogram = ImmutableHistogram.<Integer>builder(histogram) //
        .setBucketUpperBounds(bucketUpperBounds) //
        .build();
    assertThat(histogram.bucketUpperBounds()).isEqualTo(bucketUpperBounds);
    assertThat(histogram.bucketUpperBounds()).isInstanceOf(ImmutableList.class);


  }

  //
  // Tests for bucketUpperBound
  //

  @Test
  public void bucketUpperBound_shouldWork() {
    ImmutableHistogram.Builder<Byte> builder = makeHistogramOfByteBuilder();
    ImmutableHistogram<Byte> histogram = builder.build();

    assertThat(histogram.bucketUpperBound(0)).isEqualTo((byte) 0);
    assertThat(histogram.bucketUpperBound(1)).isEqualTo((byte) 0x7f);
  }

  @Test
  public void bucketUpperBound_withLastBucketIndex_shouldThrow() {
    ImmutableHistogram.Builder<Byte> builder = makeHistogramOfByteBuilder();
    ImmutableHistogram<Byte> histogram = builder.build();

    try {
      histogram.bucketUpperBound(2);
      Truth.assert_().fail("Expected an IllegalArgumentException.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat()
          .isEqualTo(ImmutableHistogram.NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE);
    }
  }

  //
  // Tests for countInBucket and totalCount
  //

  @Test
  public void countInBucket_shouldWork() {
    ImmutableHistogram<Byte> histogram = ImmutableHistogram.<Byte>builder() //
        .setCountByBucket(ImmutableList.of(42L, 37L, 17L)) //
        .setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0xff)) //
        .build();
    assertThat(histogram.countInBucket(0)).isEqualTo(42L);
    assertThat(histogram.countInBucket(1)).isEqualTo(37L);
    assertThat(histogram.countInBucket(2)).isEqualTo(17L);
  }

  @Test
  public void totalCount_shouldWork() {
    ImmutableHistogram<Integer> histogram = makeHistogramOfIntegerPower2Of2();
    assertThat(histogram.totalCount()).isEqualTo(25L);
  }

  //
  // Tests for copyOf and from
  //

  @Test
  public void copyOf_withValidHistogram_shouldWork() {
    ImmutableHistogram.Builder<Byte> builder = ImmutableHistogram.<Byte>builder();
    builder.setCountByBucket(ImmutableList.of(42L, 37L, 17L));
    builder.setBucketUpperBounds(ImmutableList.of((byte) 0x00, (byte) 0xff));
    ImmutableHistogram<Byte> histogram = builder.build();

    ImmutableHistogram<Byte> copy = ImmutableHistogram.copyOf(histogram);
    assertThat(copy).isEqualTo(histogram);
  }

  @Test(expected = NullPointerException.class)
  public void copyOf_withNullHistogram_shouldThrow() {
    ImmutableHistogram.copyOf(null);
  }

  @Test
  public void from_withInstanceThatIsNotAnImmutableHistogram_shouldWork() {
    ConcurrentHistogram<Integer> histogram = new ConcurrentHistogram<>(powerOf2IntBucketer(0, 5));
    histogram.countValue(4);
    histogram.countValue(4);
    assertThat(ImmutableHistogram.from(histogram).countInBucket(2)).isEqualTo(2);
  }

  private BucketSelector<Integer> powerOf2IntBucketer(int minPower, int numBuckets) {
    return BucketSelectors.transform(BucketSelectors.powerOf2LongValues(minPower, numBuckets),
        (i) -> (long) i, (l) -> l.intValue());
  }

  @Test
  public void from_withValidImmutableHistogram_shouldWork() {
    ImmutableHistogram<Integer> histogram = makeHistogramOfIntegerPower2Of2();
    assertThat(ImmutableHistogram.from(histogram)).isSameAs(histogram);
  }
}
