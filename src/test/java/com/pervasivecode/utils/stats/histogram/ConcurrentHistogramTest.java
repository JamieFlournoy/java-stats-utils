package com.pervasivecode.utils.stats.histogram;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import com.pervasivecode.utils.stats.histogram.ConcurrentHistogram;

public class ConcurrentHistogramTest {
  @Mock
  private BucketSelector<Float> bucketer;

  private ConcurrentHistogram<Float> histogram;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(bucketer.numBuckets()).thenReturn(37);
    histogram = new ConcurrentHistogram<>(bucketer);
  }

  @Test
  public void numBuckets_shouldMatchBucketSelector() {
    assertThat(histogram.numBuckets()).isEqualTo(37);
  }

  @Test
  public void bucketUpperBound_shouldMatchBucketSelector() {
    when(bucketer.bucketUpperBound(5)).thenReturn(12345f);
    assertThat(histogram.bucketUpperBound(5)).isWithin(0).of(12345f);
  }

  @Test
  public void countInBucket_shouldChangeAfterCountValue() {
    assertThat(histogram.countInBucket(5)).isEqualTo(0);
    when(bucketer.bucketIndexFor(-345f)).thenReturn(5);
    when(bucketer.bucketIndexFor(678f)).thenReturn(6);
    histogram.countValue(-345f);
    assertThat(histogram.countInBucket(5)).isEqualTo(1);
    histogram.countValue(678f);
    assertThat(histogram.countInBucket(6)).isEqualTo(1);
    histogram.countValue(-345f);
    assertThat(histogram.countInBucket(5)).isEqualTo(2);
  }
}
