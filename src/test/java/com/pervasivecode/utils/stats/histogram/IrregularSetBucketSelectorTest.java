package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.truth.Truth;
import com.pervasivecode.utils.stats.histogram.IrregularSetBucketSelector;

public class IrregularSetBucketSelectorTest {

  @Test
  public void constructor_withEmptyMaxValueSet_shouldWork() {
    IrregularSetBucketSelector<String> bucketer = new IrregularSetBucketSelector<>(ImmutableSortedSet.of());
    assertThat(bucketer.bucketIndexFor("Hello")).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor("Bucketing")).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor("World")).isEqualTo(0);
  }

  @Test
  public void numBuckets_withTwoUpperBounds_shouldReturnThree() {
    IrregularSetBucketSelector<Integer> bucketer =
        new IrregularSetBucketSelector<>(ImmutableSortedSet.of(-1000, 1000));
    assertThat(bucketer.numBuckets()).isEqualTo(3);
  }
  
  @Test
  public void bucketIndexFor_withSingleMaxValueSet_shouldReturnZeroOrOne() {
    IrregularSetBucketSelector<String> bucketer = new IrregularSetBucketSelector<>(ImmutableSortedSet.of("X"));
    assertThat(bucketer.bucketIndexFor("Foo")).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor("Zig!")).isEqualTo(1);
  }

  @Test
  public void bucketIndexFor_withMultipleMaxValueSet_shouldReturnCorrectBucket() {
    IrregularSetBucketSelector<String> bucketer =
        new IrregularSetBucketSelector<>(ImmutableSortedSet.of("J", "T"));
    assertThat(bucketer.bucketIndexFor("Foo")).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor("J")).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor("Jam")).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor("T")).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor("Tea")).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor("Zig!")).isEqualTo(2);
  }

  @Test
  public void maxValueForBucket_withSingleMaxValueSet_shouldReturnThatValueOrNull() {
    IrregularSetBucketSelector<String> bucketer = new IrregularSetBucketSelector<>(ImmutableSortedSet.of("X"));
    assertThat(bucketer.bucketUpperBound(0)).isEqualTo("X");
    try {
      bucketer.bucketUpperBound(1);
      Truth.assert_().fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage(IrregularSetBucketSelector.NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE);
    }
  }

  @Test
  public void maxValueForBucket_withMultipleMaxValueSet_shouldReturnCorrectMaxValue() {
    IrregularSetBucketSelector<String> bucketer =
        new IrregularSetBucketSelector<>(ImmutableSortedSet.of("J", "T"));
    assertThat(bucketer.bucketUpperBound(0)).isEqualTo("J");
    assertThat(bucketer.bucketUpperBound(1)).isEqualTo("T");
    try {
      assertThat(bucketer.bucketUpperBound(2)).isNull();
      Truth.assert_().fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessage(IrregularSetBucketSelector.NO_UPPER_BOUND_IN_LAST_BUCKET_MESSAGE);
    }
  }
}
