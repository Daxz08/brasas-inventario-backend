package com.brasas.Inventario.service;

import com.brasas.Inventario.dto.response.AlertaStockResponse;
import com.brasas.Inventario.dto.response.DashboardResponse;
import com.brasas.Inventario.dto.response.StockProductoResponse;

import java.util.List;

public interface AlertaService {

    List<StockProductoResponse> listarStockActual();

    List<AlertaStockResponse> listarAlertasStockMinimo();

    DashboardResponse obtenerDashboard();
}