package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.UsuarioRequest;
import com.brasas.Inventario.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listarTodos();

    UsuarioResponse obtenerPorId(Integer id);

    UsuarioResponse crear(UsuarioRequest request);

    UsuarioResponse actualizar(Integer id, UsuarioRequest request);

    void eliminar(Integer id);
}