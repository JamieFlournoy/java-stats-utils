package com.pervasivecode.utils.stats;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import com.pervasivecode.utils.time.api.TimeSource;

/**
 * This is a trivial DurationEstimator that just uses the total amount processed divided by the
 * total time elapsed to estimate the rate (that is, it's blind to any short-term fluctuations in
 * rate that may occur, and only examines the entire process).
 * <p>
 * Example: If an instance was created 4 seconds ago according to the TimeSource passed to the
 * constructor, and recordAmountSoFar has been called once per second with values {0, 0, 0, 1000},
 * the estimated rate returned after each recordAmountSoFar call would be {0.0f, 0.0f, 0.0f,
 * 250.0f}.
 * <p>
 * Time values are internally limited to milliseconds precision.
 */
public class SimpleDurationEstimator implements DurationEstimator {

  private final TimeSource timeSource;
  private final Instant startTime;
  private final long initialProgressValue;

  // This must not be a "long", because it needs to be modified by one thread without another thread
  // potentially reading a _partially-modified_ long value. See JLS 17.7 if you don't understand how
  // it could be partially modified:
  // https://docs.oracle.com/javase/specs/jls/se10/html/jls-17.html#jls-17.7
  //
  // This field could also be a "volatile long" since it's only modified in a simple write
  // operation, but AtomicLong#lazySet is nice-to-have, so we use AtomicLong instead.
  private final AtomicLong progressValue;

  /**
   * Create and start a SimpleDurationEstimator, with an initial progress value of zero.
   *
   * @param timeSource The source of time to be used to determine how much time has elapsed.
   */
  public SimpleDurationEstimator(TimeSource timeSource) {
    this(timeSource, 0L);
  }

  /**
   * Create and start a SimpleDurationEstimator, with an initial progress value specified by the
   * caller.
   *
   * @param timeSource The source of time to be used to determine how much time has elapsed.
   * @param initialProgressValue The initial value to be used when estimating progress.
   */
  public SimpleDurationEstimator(TimeSource timeSource, long initialProgressValue) {
    this.timeSource = checkNotNull(timeSource);
    this.startTime = timeSource.now();
    this.initialProgressValue = initialProgressValue;
    this.progressValue = new AtomicLong(initialProgressValue);
  }

  /**
   * Replace the existing progress amount value with a new one.
   *
   * @param newAmount The new progress amount.
   */
  @Override
  public void recordAmountSoFar(long newAmount) {
    this.progressValue.lazySet(newAmount);
  }

  /**
   * Get an estimate of the rate of change of the progress amount per second.
   *
   * @return The estimated rate.
   */
  @Override
  public float estimatedRateAsAmountPerSecond() {
    float elapsedSeconds = Duration.between(startTime, timeSource.now()).toMillis() / 1000f;
    if (elapsedSeconds == 0.0f) {
      return 0.0f;
    }
    return (this.progressValue.get() - initialProgressValue) / elapsedSeconds;
  }

  /**
   * Using the current estimatedRateAsAmountPerSecond, predict how much time would be needed to
   * process a specified amount starting now.
   *
   * @param amountLeft The amount whose processing time is to be estimated.
   * @return The amount of time that would be required to process the specified amount, given the
   *         current estimated rate.
   */
  @Override
  public Optional<Duration> estimateTimeToProcessAmount(long amountLeft) {
    float rate = estimatedRateAsAmountPerSecond();
    if (rate <= 0.0f) {
      return Optional.empty();
    }
    Duration estimatedSecondsRemaining = Duration.ofMillis((long) (amountLeft / rate * 1000.0f));
    return Optional.of(estimatedSecondsRemaining);
  }
}
