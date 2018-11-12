package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.truth.Truth.assertThat;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Area;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Speed;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import org.junit.Before;
import org.junit.Test;
import com.pervasivecode.utils.stats.histogram.BucketSelector;
import systems.uom.common.USCustomary;
import tec.uom.se.unit.Units;

public class QuantityBucketSelectorsTest {
  private ServiceProvider serviceProvider;

  @Before
  public void setup() {
    serviceProvider = ServiceProvider.current();
  }

  @Test
  public void powerOf2_shouldBucketCorrectly() {
    QuantityFactory<MagneticFluxDensity> quantityFactory =
        serviceProvider.getQuantityFactory(MagneticFluxDensity.class);
    Unit<MagneticFluxDensity> tesla = Units.TESLA;

    BucketSelector<Quantity<MagneticFluxDensity>> bucketer =
        QuantityBucketSelectors.powerOf2(0, tesla, quantityFactory, 10);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(0L, tesla))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1L, tesla))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(2L, tesla))).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(3L, tesla))).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(4L, tesla))).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(5L, tesla))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(8L, tesla))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(9L, tesla))).isEqualTo(4);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(16L, tesla))).isEqualTo(4);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(31L, tesla))).isEqualTo(5);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(256L, tesla))).isEqualTo(8);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(513L, tesla))).isEqualTo(9);

    bucketer = QuantityBucketSelectors.powerOf2(2, tesla, quantityFactory, 5);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(0L, tesla))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(4L, tesla))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(31L, tesla))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(513L, tesla))).isEqualTo(4);
  }

  @Test
  public void powerOf2_shouldReturnUpperBoundsCorrectly() {
    QuantityFactory<MagneticFluxDensity> quantityFactory =
        serviceProvider.getQuantityFactory(MagneticFluxDensity.class);
    Unit<MagneticFluxDensity> tesla = Units.TESLA;

    BucketSelector<Quantity<MagneticFluxDensity>> bucketer =
        QuantityBucketSelectors.powerOf2(0, tesla, quantityFactory, 10);

    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(quantityFactory.create(1L, tesla));
    assertThat(bucketer.bucketUpperBound(1)).isEqualTo(quantityFactory.create(2L, tesla));
    assertThat(bucketer.bucketUpperBound(2)).isEqualTo(quantityFactory.create(4L, tesla));
    assertThat(bucketer.bucketUpperBound(5)).isEqualTo(quantityFactory.create(32L, tesla));

    bucketer = QuantityBucketSelectors.powerOf2(2, tesla, quantityFactory, 5);
    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(quantityFactory.create(4L, tesla));
    assertThat(bucketer.bucketUpperBound(3)).isEqualTo(quantityFactory.create(32L, tesla));
  }


  @Test
  public void exponential_shouldBucketCorrectly() {
    QuantityFactory<Area> quantityFactory = serviceProvider.getQuantityFactory(Area.class);
    Unit<Area> sqFt = USCustomary.SQUARE_FOOT;

    BucketSelector<Quantity<Area>> bucketer =
        QuantityBucketSelectors.exponential(10.0, sqFt, quantityFactory, 2, 8);

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

    Unit<Area> acre = USCustomary.ACRE;
    bucketer = QuantityBucketSelectors.exponential(5, acre, quantityFactory, 0, 5);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1, acre))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(5, acre))).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(25, acre))).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(125, acre))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(126, acre))).isEqualTo(4);
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

  @Test
  public void linear_shouldBucketCorrectly() {
    QuantityFactory<Pressure> quantityFactory = serviceProvider.getQuantityFactory(Pressure.class);
    Unit<Pressure> pa = Units.PASCAL;

    BucketSelector<Quantity<Pressure>> bucketer = QuantityBucketSelectors.linear(pa,
        quantityFactory, quantityFactory.create(-1000, pa), quantityFactory.create(2000, pa), 5);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(-2000L, pa))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(-1000L, pa))).isEqualTo(0);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(-999L, pa))).isEqualTo(1);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(-1L, pa))).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(0L, pa))).isEqualTo(1);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1L, pa))).isEqualTo(2);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(999L, pa))).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1000L, pa))).isEqualTo(2);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1001L, pa))).isEqualTo(3);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(1999L, pa))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(2000L, pa))).isEqualTo(3);
    assertThat(bucketer.bucketIndexFor(quantityFactory.create(2001L, pa))).isEqualTo(4);

    assertThat(bucketer.bucketIndexFor(quantityFactory.create(3000L, pa))).isEqualTo(4);
  }

  @Test
  public void linear_shouldReturnUpperBoundsCorrectly() {
    QuantityFactory<Pressure> quantityFactory = serviceProvider.getQuantityFactory(Pressure.class);
    Unit<Pressure> pa = Units.PASCAL;

    BucketSelector<Quantity<Pressure>> bucketer = QuantityBucketSelectors.linear(pa,
        quantityFactory, quantityFactory.create(-1000, pa), quantityFactory.create(2000, pa), 5);

    assertThat(bucketer.bucketUpperBound(0)).isEqualTo(quantityFactory.create(-1000, pa));
    assertThat(bucketer.bucketUpperBound(1)).isEqualTo(quantityFactory.create(0, pa));
    assertThat(bucketer.bucketUpperBound(2)).isEqualTo(quantityFactory.create(1000, pa));
    assertThat(bucketer.bucketUpperBound(3)).isEqualTo(quantityFactory.create(2000, pa));
  }
}
