package org.example;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;
import java.util.OptionalDouble;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DistributionStatisticConfigurator implements MeterFilter {

  private static final DistributionStatisticConfig TIMER_DEFAULTS = DistributionStatisticConfig.builder()
                                                                        .percentilesHistogram(true)
                                                                        .build();

  private final boolean enableTimerHistograms;
  private final OptionalDouble scaleMaxExpectedValue;

  private final int fixedNumberOfBuckets;

  @Inject
  public DistributionStatisticConfigurator(
      @ConfigProperty(name = "micrometer.timer.default.percentile-histogram") boolean enableTimerHistograms,
      @ConfigProperty(name = "micrometer.timer.default.scale-max-expected-value") OptionalDouble scaleMaxExpectedValue,
      @ConfigProperty(name = "micrometer.fixed-number-of-buckets", defaultValue = "20") int fixedNumberOfBuckets
  ) {
    this.enableTimerHistograms = enableTimerHistograms;
    this.scaleMaxExpectedValue = Objects.requireNonNull(scaleMaxExpectedValue);
    this.fixedNumberOfBuckets = fixedNumberOfBuckets;
  }

  @Override
  public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
    if (id.getType() == Meter.Type.TIMER) {
      DistributionStatisticConfig resultConfig = config;
      if (enableTimerHistograms) {
        resultConfig = resultConfig.merge(TIMER_DEFAULTS);
      }
      if (scaleMaxExpectedValue.isPresent() && resultConfig.getMaximumExpectedValueAsDouble() != null) {
        resultConfig = DistributionStatisticConfig.builder()
                           .maximumExpectedValue(resultConfig.getMaximumExpectedValueAsDouble() * scaleMaxExpectedValue.getAsDouble())
                           .build()
                           .merge(resultConfig);
      }

      if (fixedNumberOfBuckets > 0) {
        resultConfig = DistributionStatisticConfig.builder()
                           .percentilesHistogram(false)
                           .minimumExpectedValue(resultConfig.getMinimumExpectedValueAsDouble())
                           .maximumExpectedValue(resultConfig.getMaximumExpectedValueAsDouble())
                           .build()
                           .merge(resultConfig);
      }

      return resultConfig;
    }
    return config;
  }
}
