package com.brasas.Inventario.controller;

import com.brasas.Inventario.dto.response.AlertaStockResponse;
import com.brasas.Inventario.dto.response.DashboardResponse;
import com.brasas.Inventario.dto.response.StockProductoResponse;
import com.brasas.Inventario.service.AlertaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/stock")
    public ResponseEntity<List<StockProductoResponse>> listarStock() {
        return ResponseEntity.ok(alertaService.listarStockActual());
    }

    @GetMapping("/alertas/stock-minimo")
    public ResponseEntity<List<AlertaStockResponse>> listarAlertas() {
        return ResponseEntity.ok(alertaService.listarAlertasStockMinimo());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(alertaService.obtenerDashboard());
    }
}