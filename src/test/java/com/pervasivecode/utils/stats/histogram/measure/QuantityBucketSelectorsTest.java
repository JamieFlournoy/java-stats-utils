package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.truth.Truth.assertThat;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Area;
import javax.measure.quantity.Speed;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import org.junit.Before;
import org.junit.Test;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import systems.uom.common.USCustomary;

public class QuantityBucketSelectorsTest {
  private ServiceProvider serviceProvider;

  @Before
  public void setup() {
    serviceProvider = ServiceProvider.current();
  }

  @Test
  public void exponential_shouldBucketCorrectly() {
    QuantityFactory<Area> quantityFactory = serviceProvider.getQuantityFactory(Area.class);
    Unit<Area> sqFt = USCustomary.SQUARE_FOOT;

    BucketSelector<Quantity<Area>> bucketer = QuantityBucketSelectors.exponential(10.0,
        sqFt, quantityFactory, 2, 8);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(99, sqFt))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(100, sqFt))).isEqualTo(0);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(101, sqFt))).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1000, sqFt))).isEqualTo(1);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1_001, sqFt))).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(10_000, sqFt))).isEqualTo(2);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(10_001, sqFt))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(100_000, sqFt))).isEqualTo(3);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(100_001, sqFt))).isEqualTo(4);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1_000_000, sqFt))).isEqualTo(4);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1_000_001, sqFt))).isEqualTo(5);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(10_000_000, sqFt))).isEqualTo(5);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(10_000_001, sqFt))).isEqualTo(6);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(100_000_000, sqFt))).isEqualTo(6);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(100_000_001, sqFt))).isEqualTo(7);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1_000_000_000, sqFt))).isEqualTo(7);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(10_000_000_000L, sqFt))).isEqualTo(7);
  }

  @Test
  public void exponential_shouldReturnUpperBoundsCorrectly() {
    QuantityFactory<Speed> quantityFactory = serviceProvider.getQuantityFactory(Speed.class);
    Unit<Speed> kn = USCustomary.KNOT;
    BucketSelector<Quantity<Speed>> bucketer =
        QuantityBucketSelectors.exponential(3, kn, quantityFactory, 0, 6);

    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(quantityFactory.create(1, kn));
    assertThat(bucketer.bucketUpperBound(1)).isEqualTo(quantityFactory.create(3, kn));
    assertThat(bucketer.bucketUpperBound(2)).isEqualTo(quantityFactory.create(9, kn));
    assertThat(bucketer.bucketUpperBound(3)).isEqualTo(quantityFactory.create(27, kn));
    assertThat(bucketer.bucketUpperBound(4)).isEqualTo(quantityFactory.create(81, kn));
  }
}
