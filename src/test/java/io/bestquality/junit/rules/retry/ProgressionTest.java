package io.bestquality.junit.rules.retry;

import org.junit.Before;
import org.junit.Test;

import static io.bestquality.junit.rules.retry.Progression.ARITHMETIC;
import static io.bestquality.junit.rules.retry.Progression.CONSTANT;
import static io.bestquality.junit.rules.retry.Progression.GEOMETRIC;
import static org.assertj.core.api.Assertions.assertThat;

public class ProgressionTest {

    private long pause;

    @Before
    public void setUp() {
        pause = 1000;
    }

    @Test
    public void shouldComputeConstantProgression() {
        Progression progression = CONSTANT;

        assertThat(progression.next(1, pause))
                .isEqualTo(1000);
        assertThat(progression.next(2, pause))
                .isEqualTo(1000);
        assertThat(progression.next(3, pause))
                .isEqualTo(1000);
    }

    @Test
    public void shouldComputeArithmeticProgression() {
        Progression progression = ARITHMETIC;

        assertThat(progression.next(1, pause))
                .isEqualTo(1000);
        assertThat(progression.next(2, pause))
                .isEqualTo(2000);
        assertThat(progression.next(3, pause))
                .isEqualTo(3000);
    }

    @Test
    public void shouldComputeGeometricProgression() {
        Progression progression = GEOMETRIC;

        assertThat(progression.next(1, pause))
                .isEqualTo(1000);
        assertThat(progression.next(2, pause))
                .isEqualTo(4000);
        assertThat(progression.next(3, pause))
                .isEqualTo(9000);
    }
}
