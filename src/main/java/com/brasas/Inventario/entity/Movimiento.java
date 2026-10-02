package com.brasas.Inventario.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento = LocalDateTime.now();

    @Column(name = "observacion", length = 250)
    private String observacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoMovimiento estado = EstadoMovimiento.CONFIRMADO;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Builder.Default
    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleMovimiento> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fechaMovimiento == null) fechaMovimiento = LocalDateTime.now();
        if (estado == null) estado = EstadoMovimiento.CONFIRMADO;
    }

    public enum TipoMovimiento {
        ENTRADA, SALIDA, MERMA, AJUSTE
    }

    public enum EstadoMovimiento {
        CONFIRMADO, ANULADO
    }
}