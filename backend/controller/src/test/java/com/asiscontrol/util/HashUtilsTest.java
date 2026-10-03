package com.asiscontrol.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashUtilsTest {

    @Test
    void createsStableSha256WithoutReturningOriginalValue() {
        String first = HashUtils.sha256("token-de-prueba");
        String second = HashUtils.sha256("token-de-prueba");

        assertThat(first)
                .hasSize(64)
                .isEqualTo(second)
                .isNotEqualTo("token-de-prueba");
    }
}
