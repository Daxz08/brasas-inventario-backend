package com.brasas.Inventario.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MovimientoRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento; // ENTRADA, SALIDA, MERMA, AJUSTE

    private String observacion;

    private Integer idProveedor; // Obligatorio solo para ENTRADA

    @NotEmpty(message = "Debe incluir al menos un producto")
    @Valid
    private List<DetalleMovimientoRequest> detalles;
}