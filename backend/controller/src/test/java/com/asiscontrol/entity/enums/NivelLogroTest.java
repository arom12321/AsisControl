package com.asiscontrol.entity.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NivelLogroTest {

    @Test
    void convertsNumericAverageToExpectedAchievementLevel() {
        assertThat(NivelLogro.desdePromedio(4.0)).isEqualTo(NivelLogro.AD);
        assertThat(NivelLogro.desdePromedio(3.5)).isEqualTo(NivelLogro.AD);
        assertThat(NivelLogro.desdePromedio(3.49)).isEqualTo(NivelLogro.A);
        assertThat(NivelLogro.desdePromedio(2.5)).isEqualTo(NivelLogro.A);
        assertThat(NivelLogro.desdePromedio(2.49)).isEqualTo(NivelLogro.B);
        assertThat(NivelLogro.desdePromedio(1.5)).isEqualTo(NivelLogro.B);
        assertThat(NivelLogro.desdePromedio(1.49)).isEqualTo(NivelLogro.C);
    }
}
