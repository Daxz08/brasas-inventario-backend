package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.ProductoRequest;
import com.brasas.Inventario.dto.response.ProductoResponse;

import java.util.List;

public interface ProductoService {

    List<ProductoResponse> listarTodos();

    List<ProductoResponse> listarPorCategoria(Integer idCategoria);

    List<ProductoResponse> buscarPorNombre(String nombre);

    ProductoResponse obtenerPorId(Integer id);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Integer id, ProductoRequest request);

    void eliminar(Integer id);
}