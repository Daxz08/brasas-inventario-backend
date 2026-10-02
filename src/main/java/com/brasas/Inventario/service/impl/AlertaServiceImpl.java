package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.response.AlertaStockResponse;
import com.brasas.Inventario.dto.response.DashboardResponse;
import com.brasas.Inventario.dto.response.StockProductoResponse;
import com.brasas.Inventario.entity.Movimiento;
import com.brasas.Inventario.entity.Producto;
import com.brasas.Inventario.repository.MovimientoRepository;
import com.brasas.Inventario.repository.ProductoRepository;
import com.brasas.Inventario.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertaServiceImpl implements AlertaService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    public List<StockProductoResponse> listarStockActual() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::toStockResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlertaStockResponse> listarAlertasStockMinimo() {
        return productoRepository.findProductosConStockBajo().stream()
                .map(p -> AlertaStockResponse.builder()
                        .idProducto(p.getIdProducto())
                        .codigoSku(p.getCodigoSku())
                        .nombre(p.getNombre())
                        .stockActual(p.getStockActual())
                        .stockMinimo(p.getStockMinimo())
                        .faltante(p.getStockMinimo().subtract(p.getStockActual()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public DashboardResponse obtenerDashboard() {
        Long totalProductos = (long) productoRepository.findByActivoTrue().size();
        Long stockBajo = (long) productoRepository.findProductosConStockBajo().size();

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);
        Long movimientosHoy = (long) movimientoRepository
                .findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(inicioHoy, finHoy).size();

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Long mermasMes = movimientoRepository
                .findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(inicioMes, LocalDateTime.now())
                .stream()
                .filter(m -> m.getTipoMovimiento() == Movimiento.TipoMovimiento.MERMA)
                .filter(m -> m.getEstado() == Movimiento.EstadoMovimiento.CONFIRMADO)
                .count();

        return DashboardResponse.builder()
                .totalProductos(totalProductos)
                .productosStockBajo(stockBajo)
                .movimientosHoy(movimientosHoy)
                .mermasDelMes(mermasMes)
                .build();
    }

    private StockProductoResponse toStockResponse(Producto p) {
        String estado;
        if (p.getStockActual().compareTo(BigDecimal.ZERO) == 0) {
            estado = "CRITICO";
        } else if (p.getStockActual().compareTo(p.getStockMinimo()) <= 0) {
            estado = "BAJO";
        } else {
            estado = "NORMAL";
        }

        return StockProductoResponse.builder()
                .idProducto(p.getIdProducto())
                .codigoSku(p.getCodigoSku())
                .nombre(p.getNombre())
                .nombreCategoria(p.getCategoria().getNombre())
                .unidadMedida(p.getUnidadMedida())
                .stockActual(p.getStockActual())
                .stockMinimo(p.getStockMinimo())
                .estado(estado)
                .build();
    }
}