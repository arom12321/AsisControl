package com.asiscontrol.entity.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OperadorComparacionTest {

    @ParameterizedTest
    @CsvSource({
            "MENOR_QUE,4,5,true",
            "MENOR_QUE,5,5,false",
            "MENOR_IGUAL,5,5,true",
            "MAYOR_QUE,6,5,true",
            "MAYOR_QUE,5,5,false",
            "MAYOR_IGUAL,5,5,true",
            "IGUAL,5,5,true",
            "IGUAL,5,6,false"
    })
    void appliesConfiguredComparison(
            OperadorComparacion operator,
            double value,
            double threshold,
            boolean expected
    ) {
        assertThat(operator.cumple(value, threshold)).isEqualTo(expected);
    }
}
