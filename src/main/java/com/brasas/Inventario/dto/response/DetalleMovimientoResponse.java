package com.brasas.Inventario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMovimientoResponse {

    private Integer idDetalle;
    private Integer idProducto;
    private String nombreProducto;
    private String codigoSku;
    private BigDecimal cantidad;
    private BigDecimal precioCosto;
    private LocalDate fechaVencimiento;
}