package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.LoginRequest;
import com.brasas.Inventario.dto.response.LoginResponse;
import com.brasas.Inventario.dto.response.UsuarioResponse;

public interface AuthService {

    LoginResponse iniciarLogin(LoginRequest request);

    LoginResponse verificarCodigo(String nombreUsuario, String codigo);

    UsuarioResponse obtenerUsuarioActual(String username);
}