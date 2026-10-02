package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.request.MovimientoRequest;
import com.brasas.Inventario.dto.response.MovimientoResponse;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoService {

    List<MovimientoResponse> listarTodos();

    List<MovimientoResponse> listarPorTipo(String tipo);

    List<MovimientoResponse> listarPorRangoFechas(LocalDate desde, LocalDate hasta);

    MovimientoResponse obtenerPorId(Integer id);

    MovimientoResponse registrar(MovimientoRequest request, String username);
}