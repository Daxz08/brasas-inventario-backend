package com.brasas.Inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequest {

    @NotBlank(message = "El RUC es obligatorio")
    @Size(max = 20, message = "El RUC no puede exceder 20 caracteres")
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 150, message = "La razón social no puede exceder 150 caracteres")
    private String razonSocial;

    @Size(max = 100)
    private String contacto;

    @Size(max = 20)
    private String telefono;

    @Size(max = 200)
    private String direccion;
}