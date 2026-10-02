package com.brasas.Inventario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaStockResponse {

    private Integer idProducto;
    private String codigoSku;
    private String nombre;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal faltante; // cuánto falta para llegar al mínimo
}