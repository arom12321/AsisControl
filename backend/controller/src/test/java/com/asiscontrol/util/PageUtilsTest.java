package com.asiscontrol.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PageUtilsTest {

    @Test
    void limitsPageSizeAndRejectsUnknownSortField() {
        Pageable pageable = PageUtils.create(
                -2,
                1000,
                "campoNoPermitido",
                Sort.Direction.DESC,
                Set.of("nombre"),
                "nombre"
        );

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(PageUtils.MAX_PAGE_SIZE);
        assertThat(pageable.getSort().getOrderFor("nombre")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("nombre").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }
}
