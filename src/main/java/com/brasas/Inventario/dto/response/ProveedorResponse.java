package com.brasas.Inventario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Integer idProveedor;
    private String ruc;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}