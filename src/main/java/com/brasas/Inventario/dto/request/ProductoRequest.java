package com.brasas.Inventario.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {

    @NotBlank(message = "El código SKU es obligatorio")
    @Size(max = 30)
    private String codigoSku;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    private String nombre;

    @Size(max = 250)
    private String descripcion;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 20)
    private String unidadMedida;

    @NotNull(message = "El stock inicial es obligatorio")
    @DecimalMin(value = "0.0", message = "El stock no puede ser negativo")
    private BigDecimal stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
    private BigDecimal stockMinimo;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioUnitario;

    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;

    private Boolean activo = true;
}