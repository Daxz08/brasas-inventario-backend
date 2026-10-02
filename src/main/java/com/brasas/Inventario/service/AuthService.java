package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.LoginRequest;
import com.brasas.Inventario.dto.response.LoginResponse;
import com.brasas.Inventario.dto.response.UsuarioResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    UsuarioResponse obtenerUsuarioActual(String username);
}