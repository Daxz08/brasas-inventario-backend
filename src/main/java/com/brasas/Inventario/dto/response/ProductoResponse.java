package com.brasas.Inventario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    private Integer idProducto;
    private String codigoSku;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal precioUnitario;
    private Integer idCategoria;
    private String nombreCategoria;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaModificacion;
}