package io.bestquality.junit.rules.failure;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import static io.bestquality.junit.rules.failure.Policies.releasing;
import static org.assertj.core.api.Assertions.assertThat;

public class PoliciesTest {

    @Rule
    public final RestoreSystemProperties properties = new RestoreSystemProperties();

    @Test
    public void shouldIndicateReleasingBasedOnEnvironmentProperty() {
        System.setProperty("project.version", "1.0.1");

        assertThat(releasing())
                .isTrue();
    }

    @Test
    public void shouldIndicateNotReleasingBasedOnEnvironmentProperty() {
        System.setProperty("project.version", "1.0.1-SNAPSHOT");

        assertThat(releasing())
                .isFalse();
    }

    @Test
    public void shouldIndicateNotReleasingBasedWhenEnvironmentPropertyNotSet() {
        System.clearProperty("project.version");

        assertThat(releasing())
                .isFalse();
    }
}
