package org.example;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.micrometer.core.instrument.Timer;

@ApplicationScoped
public class SomeClass {
  private final Timer helloTimer;

  @Inject
  public SomeClass(MeterRegistry meterRegistry) {
    this.helloTimer = Timer.builder("helloTimer")
                          .register(meterRegistry);
  }

  public String hello() {
    return helloTimer.record(() -> "hello");
  }
}
