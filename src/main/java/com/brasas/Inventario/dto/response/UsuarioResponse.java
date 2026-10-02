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
public class UsuarioResponse {

    private Integer idUsuario;
    private String nombreUsuario;
    private String nombres;
    private String apellidos;
    private String email;
    private Integer idRol;
    private String nombreRol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}