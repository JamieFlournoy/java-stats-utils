package com.pervasivecode.utils.stats.histogram.measure;

import static com.google.common.truth.Truth.assertThat;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.spi.QuantityFactory;
import javax.measure.spi.ServiceProvider;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import com.pervasivecode.utils.stats.histogram.ImmutableHistogram;
import tec.uom.se.unit.Units;

public class ImmutableQuantityHistogramTest {
  private ServiceProvider serviceProvider;
  private QuantityFactory<MagneticFluxDensity> quantityFactory;
  private Unit<MagneticFluxDensity> tesla;
  private ImmutableHistogram<Long> plainHistogram;

  @Before
  public void setup() {
    serviceProvider = ServiceProvider.current();
    quantityFactory = serviceProvider.getQuantityFactory(MagneticFluxDensity.class);
    tesla = Units.TESLA;
    plainHistogram = ImmutableHistogram.<Long>builder()
        .setBucketUpperBounds(ImmutableList.of(5L, 10L, 15L, 20L, 25L))
        .setCountByBucket(ImmutableList.of(3L, 4L, 5L, 4L, 3L, 2L)).build();
  }

  @Test
  public void of_shouldRequireHistogramParam() {
    try {
      ImmutableQuantityHistogram.of(null, tesla, quantityFactory);
      Truth.assert_().fail("Expected null pointer exception due to null histogram argument");
    } catch (NullPointerException npe) {
      // expected, so do nothing.
    }
  }

  @Test
  public void of_shouldRequireBaseUnitParam() {
    try {
      ImmutableQuantityHistogram.of(plainHistogram, null, quantityFactory);
      Truth.assert_().fail("Expected null pointer exception due to null baseUnit argument");
    } catch (NullPointerException npe) {
      // expected, so do nothing.
    }
  }

  @Test
  public void of_shouldRequireQuantityFactoryParam() {
    try {
      ImmutableQuantityHistogram.of(plainHistogram, tesla, null);
      Truth.assert_().fail("Expected null pointer exception due to null quantityFactory argument");
    } catch (NullPointerException npe) {
      // expected, so do nothing.
    }
  }

  @Test
  public void of_withValidArgs_shouldProduceWorking() {
    ImmutableHistogram<Quantity<MagneticFluxDensity>> histogram =
        ImmutableQuantityHistogram.of(plainHistogram, tesla, quantityFactory);

    assertThat(histogram.bucketUpperBound(3)).isEqualTo(quantityFactory.create(20L, tesla));
    assertThat(histogram.bucketUpperBound(4)).isEqualTo(quantityFactory.create(25L, tesla));

    assertThat(histogram.countInBucket(3)).isEqualTo(4L);
    assertThat(histogram.countInBucket(5)).isEqualTo(2L);
  }
}
