 package com.pervasivecode.utils.stats;

import java.time.Duration;
import java.util.Optional;

/**
 * This object can estimate the rate at which a repeatedly provided "progress so far" scalar value
 * is currently changing, and can estimate how long it will take for that value to reach a specified
 * target value.
 */
public interface DurationEstimator {
  /**
   * Set the "progress so far" counter to the specified value.
   *
   * @param amountSoFar The current total amount of progress in whatever units the caller wants to
   *        track.
   */
  public void recordAmountSoFar(long amountSoFar);

  /**
   * Return an estimate of the current rate of progress, in terms of user-defined progress units per
   * second.
   *
   * @return The estimate, in units of amount per second.
   */
  public float estimatedRateAsAmountPerSecond();

  /**
   * Return an estimate of the amount of time that must elapse starting from the current time, in
   * order for the specified amount to be processed.
   * <p>
   * Example: if estimatedRateAsAmountPerSecond() would return 4.0f, and the amountLeft provided is
   * 44, this method will return a Duration of 11 seconds.
   *
   * @param amountLeft The amount that remains to be processed, and about which the caller wants a
   *        duration estimate.
   * @return The estimated duration. If it is impossible to compute an estimate (such as because the
   *         rate is 0), Optional.empty is returned.
   */
  public Optional<Duration> estimateTimeToProcessAmount(long amountLeft);
}
