package com.brasas.Inventario.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50)
    private String nombreUsuario;

    // Obligatoria solo al crear. En actualizar puede ir vacía para no cambiarla.
    @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    private String apellidos;

    @Email(message = "El email no es válido")
    @Size(max = 120)
    private String email;

    @NotNull(message = "El rol es obligatorio")
    private Integer idRol;

    private Boolean activo = true;
}