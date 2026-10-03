package com.asiscontrol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class AsisControlApplicationTest {

    @Test
    void contextLoads() {
        // El arranque valida el cableado, la seguridad y todo el mapeo JPA.
    }
}
