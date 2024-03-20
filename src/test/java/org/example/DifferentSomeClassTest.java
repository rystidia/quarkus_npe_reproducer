package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(DifferentSomeClassTest.TestProfile.class)
class DifferentSomeClassTest {

  @Inject
  SomeClass someClass;

  @Test
  void test() {
    assertThat(someClass.hello().equals("hello")).isTrue();
  }

  public static class TestProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      return Map.of("micrometer.timer.default.percentile-histogram", "true");
    }
  }
}
