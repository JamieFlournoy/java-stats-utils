package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;
import com.google.common.truth.Truth;

public class FunctionBasedBucketSelectorTest {
  private void checkInvalidNumBuckets(int numBuckets) {
    try {
      new FunctionBasedBucketSelector<>((v) -> v, (v) -> v, numBuckets);
      Truth.assert_().fail("Expected exception due to the invalid numBuckets value.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("numBuckets");
    }
  }

  @Test
  public void constructor_withInvalidNumBuckets_shouldThrow() {
    checkInvalidNumBuckets(-1);
    checkInvalidNumBuckets(0);
  }

  @Test
  public void bucketUpperBound_withLastBucketIndex_shouldThrow() {
    BucketSelector<Integer> selector =
        new FunctionBasedBucketSelector<>((v) -> v, (v) -> v, 4);
    try {
      selector.bucketUpperBound(3);
      Truth.assert_().fail("Expected an exception since the last bucket has no upper bound.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("no upper bound");
    }
  }
}
