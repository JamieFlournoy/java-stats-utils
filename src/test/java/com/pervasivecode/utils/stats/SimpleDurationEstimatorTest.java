package com.pervasivecode.utils.stats;

import static com.google.common.truth.Truth.assertThat;
import java.time.Duration;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import com.pervasivecode.utils.stats.SimpleDurationEstimator;
import com.pervasivecode.utils.time.testing.FakeTimeSource;

public class SimpleDurationEstimatorTest {
  private FakeTimeSource timeSource;
  private SimpleDurationEstimator estimator;

  @Before
  public void setup() {
    this.timeSource = new FakeTimeSource(false);
    this.estimator = new SimpleDurationEstimator(timeSource);
  }

  @Test
  public void elapsedRate_shouldBeBasedOnTimeSourceAndAmountSoFar() {
    // Time elapsed = 0, progress amount so far = 0
    assertThat(estimator.estimatedRateAsAmountPerSecond()).isWithin(0.001f).of(0f);

    timeSource.advance(Duration.ofSeconds(1));
    // t = 1s, p = 0
    assertThat(estimator.estimatedRateAsAmountPerSecond()).isWithin(0.001f).of(0f);

    estimator.recordAmountSoFar(1);
    // t = 1s, p = 1
    assertThat(estimator.estimatedRateAsAmountPerSecond()).isWithin(0.001f).of(1.0f);

    timeSource.advance(Duration.ofSeconds(1));
    // t = 2s, p = 1
    assertThat(estimator.estimatedRateAsAmountPerSecond()).isWithin(0.001f).of(0.5f);

    estimator.recordAmountSoFar(2);
    // t = 2s, p = 2
    assertThat(estimator.estimatedRateAsAmountPerSecond()).isWithin(0.001f).of(1.0f);

    timeSource.advance(Duration.ofSeconds(1));
    timeSource.advance(Duration.ofSeconds(1));
    timeSource.advance(Duration.ofSeconds(1));
    // t = 5s, p = 2
    assertThat(estimator.estimatedRateAsAmountPerSecond()).isWithin(0.001f).of(0.4f);
  }

  @Test
  public void estimateSecondsLeft_shouldBeBasedOnTimeSourceAndAmountSoFarAndAmountLeft() {
    assertThat(estimator.estimateTimeToProcessAmount(1).isPresent()).isFalse();
    assertThat(estimator.estimateTimeToProcessAmount(100).isPresent()).isFalse();

    estimator.recordAmountSoFar(1);
    // Time elapsed = 0, progress amount so far = 1
    assertThat(estimator.estimateTimeToProcessAmount(1).isPresent()).isFalse();

    Duration someTimeStep = Duration.ofMillis(245);
    timeSource.advance(someTimeStep);
    // t = 245ms, p = 1

    Optional<Duration> estimate = estimator.estimateTimeToProcessAmount(1);
    assertThat(estimate.isPresent()).isTrue();
    assertThat(estimate.get()).isEqualTo(Duration.ofMillis(245));

    estimate = estimator.estimateTimeToProcessAmount(10);
    assertThat(estimate.isPresent()).isTrue();
    assertThat(estimate.get()).isEqualTo(Duration.ofMillis(2450));

    timeSource.advance(someTimeStep);
    estimator.recordAmountSoFar(4);
    // now t = 490ms, p = 4

    estimate = estimator.estimateTimeToProcessAmount(1);
    assertThat(estimate.isPresent()).isTrue();
    assertThat(estimate.get()).isEqualTo(Duration.ofMillis(122));

    estimate = estimator.estimateTimeToProcessAmount(10);
    assertThat(estimate.isPresent()).isTrue();
    assertThat(estimate.get()).isEqualTo(Duration.ofMillis(1225));
  }
}
