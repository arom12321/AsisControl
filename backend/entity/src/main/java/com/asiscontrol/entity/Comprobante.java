package com.asiscontrol.entity;


import com.asiscontrol.entity.ArchivoAdjunto;
import com.asiscontrol.entity.AuditableEntity;
import com.asiscontrol.entity.enums.TipoComprobante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Comprobante", uniqueConstraints = {
        @UniqueConstraint(name = "uk_comprobante_transaccion", columnNames = "id_transaccion_pago"),
        @UniqueConstraint(name = "uk_comprobante_serie_numero", columnNames = {"serie", "numero"})
})
public class Comprobante extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_transaccion_pago", nullable = false)
    private TransaccionPago transaccionPago;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_archivo_adjunto")
    private ArchivoAdjunto archivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 40)
    private TipoComprobante tipoComprobante;

    @Column(name = "serie", nullable = false, length = 20)
    private String serie;

    @Column(name = "numero", nullable = false, length = 30)
    private String numero;

    @Column(name = "fecha_emision", nullable = false)
    private Instant fechaEmision;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;
}
