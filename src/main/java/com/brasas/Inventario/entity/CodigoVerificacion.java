package com.brasas.Inventario.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_verificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoVerificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo")
    private Integer idCodigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "codigo", nullable = false, length = 6)
    private String codigo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado", nullable = false)
    private Boolean usado = false;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (usado == null) usado = false;
    }
}