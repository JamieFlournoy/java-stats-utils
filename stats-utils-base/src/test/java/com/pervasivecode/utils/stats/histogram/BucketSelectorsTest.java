package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.BucketSelectors;

public class BucketSelectorsTest {
  @Test
  public void powerOf2_shouldBucketCorrectly() {
    BucketSelector<Long> bucketer = BucketSelectors.powerOf2LongValues(0, 10);
    assertThat(bucketer.bucketIndexFor(0L)).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(1L)).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(2L)).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(3L)).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(4L)).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(5L)).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(8L)).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(9L)).isEqualTo(4);
    assertThat(bucketer.bucketIndexFor(16L)).isEqualTo(4);
    assertThat(bucketer.bucketIndexFor(31L)).isEqualTo(5);
    assertThat(bucketer.bucketIndexFor(256L)).isEqualTo(8);
    assertThat(bucketer.bucketIndexFor(513L)).isEqualTo(9);

    bucketer = BucketSelectors.powerOf2LongValues(2, 5);
    assertThat(bucketer.bucketIndexFor(0L)).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(4L)).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(31L)).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(513L)).isEqualTo(4);
  }

  @Test
  public void powerOf2_shouldReturnUpperBoundsCorrectly() {
    BucketSelector<Long> bucketer = BucketSelectors.powerOf2LongValues(0, 10);
    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(1L);
    assertThat(bucketer.bucketUpperBound(1)).isEqualTo(2L);
    assertThat(bucketer.bucketUpperBound(2)).isEqualTo(4L);
    assertThat(bucketer.bucketUpperBound(5)).isEqualTo(32L);

    bucketer = BucketSelectors.powerOf2LongValues(2, 5);
    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(4L);
    assertThat(bucketer.bucketUpperBound(3)).isEqualTo(32L);
  }


  @Test
  public void linear_shouldBucketCorrectly() {
    BucketSelector<Long> bucketer = BucketSelectors.linearLongValues(-1000, 2000, 5);

    assertThat(bucketer.bucketIndexFor(-2000L)).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(-1000L)).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(-999L)).isEqualTo(1);

    assertThat(bucketer.bucketIndexFor(-1L)).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(0L)).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(1L)).isEqualTo(2);

    assertThat(bucketer.bucketIndexFor(999L)).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(1000L)).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(1001L)).isEqualTo(3);

    assertThat(bucketer.bucketIndexFor(1999L)).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(2000L)).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(2001L)).isEqualTo(4);

    assertThat(bucketer.bucketIndexFor(3000L)).isEqualTo(4);
  }

  @Test
  public void linear_shouldReturnUpperBoundsCorrectly() {
    BucketSelector<Long> bucketer = BucketSelectors.linearLongValues(-1000, 2000, 5);
    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(-1000);
    assertThat(bucketer.bucketUpperBound(1)).isEqualTo(0);
    assertThat(bucketer.bucketUpperBound(2)).isEqualTo(1000);
    assertThat(bucketer.bucketUpperBound(3)).isEqualTo(2000);
  }
}
