package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;

public class HistogramsTest {

  private Histogram<String> buildValidHistogram() {
    ImmutableList<String> bucketUpperBounds = ImmutableList.of("L");
    ImmutableList<Long> countByBucket = ImmutableList.of(3L, 5L);
    
    Histogram<String> histogram = ImmutableHistogram.<String>builder()
        .setBucketUpperBounds(bucketUpperBounds)
        .setCountByBucket(countByBucket)
        .build();
    return histogram;
  }
  
  @Test
  public void transformValues_withNullInput_shouldThrow() {
    try {
      Histograms.transformValues(null, (v) -> v);
      Truth.assert_().fail("Expected exception due to null histogram parameter.");
    } catch (NullPointerException npe) {
      assertThat(npe).hasMessageThat().contains("histogram");
    }
  }

  @Test
  public void transformValues_withNullTransformation_shouldThrow() {
    try {
      Histograms.transformValues(buildValidHistogram(), null);
      Truth.assert_().fail("Expected exception due to null function parameter.");
    } catch (NullPointerException npe) {
      assertThat(npe).hasMessageThat().contains("transform");
    }
  }

  @Test
  public void transformValues_withValidInputAndFunction_shouldWork() {
    Histogram<String> transformed =
        Histograms.transformValues(buildValidHistogram(), String::toLowerCase);

    assertThat(transformed.numBuckets()).isEqualTo(2);
    assertThat(transformed.bucketUpperBound(0)).isEqualTo("l");
    assertThat(transformed.countInBucket(0)).isEqualTo(3L);
    assertThat(transformed.countInBucket(1)).isEqualTo(5L);

    try {
      transformed.bucketUpperBound(1);
      Truth.assert_().fail("Expected exception due to last bucket index having no upper bound.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("last bucket");
    }
  }
}
