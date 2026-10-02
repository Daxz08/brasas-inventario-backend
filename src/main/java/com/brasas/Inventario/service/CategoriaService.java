package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.CategoriaRequest;
import com.brasas.Inventario.dto.response.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResponse> listarTodas();

    CategoriaResponse obtenerPorId(Integer id);

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Integer id, CategoriaRequest request);

    void eliminar(Integer id);
}