package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.ProveedorRequest;
import com.brasas.Inventario.dto.response.ProveedorResponse;

import java.util.List;

public interface ProveedorService {

    List<ProveedorResponse> listarTodos();

    ProveedorResponse obtenerPorId(Integer id);

    ProveedorResponse crear(ProveedorRequest request);

    ProveedorResponse actualizar(Integer id, ProveedorRequest request);

    void eliminar(Integer id);
}