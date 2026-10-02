package com.brasas.Inventario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {

    private Integer idMovimiento;
    private String tipoMovimiento;
    private LocalDateTime fechaMovimiento;
    private String observacion;
    private String estado;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idProveedor;
    private String razonSocialProveedor;
    private List<DetalleMovimientoResponse> detalles;
}