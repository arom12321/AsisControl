package com.asiscontrol.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "LimiteRecuperacion")
public class LimiteRecuperacion {
    @Id
    @Column(length = 64)
    private String clave;

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private int cantidad;
}
