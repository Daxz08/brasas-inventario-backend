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
public class StockProductoResponse {

    private Integer idProducto;
    private String codigoSku;
    private String nombre;
    private String nombreCategoria;
    private String unidadMedida;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private String estado; // NORMAL, BAJO, CRITICO
}