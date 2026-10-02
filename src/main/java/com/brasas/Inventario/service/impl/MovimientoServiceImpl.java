package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.DetalleMovimientoRequest;
import com.brasas.Inventario.dto.request.MovimientoRequest;
import com.brasas.Inventario.dto.response.DetalleMovimientoResponse;
import com.brasas.Inventario.dto.response.MovimientoResponse;
import com.brasas.Inventario.entity.*;
import com.brasas.Inventario.exception.BusinessException;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.MovimientoRepository;
import com.brasas.Inventario.repository.ProductoRepository;
import com.brasas.Inventario.repository.ProveedorRepository;
import com.brasas.Inventario.repository.UsuarioRepository;
import com.brasas.Inventario.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<MovimientoResponse> listarTodos() {
        return movimientoRepository.findByEstadoOrderByFechaMovimientoDesc(
                        Movimiento.EstadoMovimiento.CONFIRMADO)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoResponse> listarPorTipo(String tipo) {
        Movimiento.TipoMovimiento tipoEnum;
        try {
            tipoEnum = Movimiento.TipoMovimiento.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de movimiento inválido: " + tipo);
        }
        return movimientoRepository.findByTipoMovimientoAndEstadoOrderByFechaMovimientoDesc(
                        tipoEnum, Movimiento.EstadoMovimiento.CONFIRMADO)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovimientoResponse> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        LocalDateTime desdeDt = desde.atStartOfDay();
        LocalDateTime hastaDt = hasta.atTime(LocalTime.MAX);
        return movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(desdeDt, hastaDt)
                .stream()
                .filter(m -> m.getEstado() == Movimiento.EstadoMovimiento.CONFIRMADO)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MovimientoResponse obtenerPorId(Integer id) {
        Movimiento m = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con ID: " + id));
        return toResponse(m);
    }

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request, String username) {

        // 1. Validar tipo de movimiento
        Movimiento.TipoMovimiento tipo;
        try {
            tipo = Movimiento.TipoMovimiento.valueOf(request.getTipoMovimiento().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de movimiento inválido: " + request.getTipoMovimiento());
        }

        // 2. Obtener usuario autenticado
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // 3. Validar proveedor según tipo
        Proveedor proveedor = null;
        if (tipo == Movimiento.TipoMovimiento.ENTRADA) {
            if (request.getIdProveedor() == null) {
                throw new BusinessException("El proveedor es obligatorio para movimientos de ENTRADA");
            }
            proveedor = proveedorRepository.findById(request.getIdProveedor())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Proveedor no encontrado con ID: " + request.getIdProveedor()));
        } else if (request.getIdProveedor() != null) {
            throw new BusinessException("El proveedor solo aplica para movimientos de ENTRADA");
        }

        // 4. Validar observación para MERMA y AJUSTE
        if ((tipo == Movimiento.TipoMovimiento.MERMA || tipo == Movimiento.TipoMovimiento.AJUSTE)
                && (request.getObservacion() == null || request.getObservacion().isBlank())) {
            throw new BusinessException("La observación (motivo) es obligatoria para " + tipo);
        }

        // 5. Crear cabecera del movimiento
        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(tipo)
                .fechaMovimiento(LocalDateTime.now())
                .observacion(request.getObservacion())
                .usuario(usuario)
                .proveedor(proveedor)
                .estado(Movimiento.EstadoMovimiento.CONFIRMADO)
                .detalles(new ArrayList<>())
                .build();

        // 6. Procesar cada detalle y actualizar stock
        for (DetalleMovimientoRequest detReq : request.getDetalles()) {
            Producto producto = productoRepository.findById(detReq.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + detReq.getIdProducto()));

            BigDecimal cantidad = detReq.getCantidad();

            // Aplicar lógica según tipo de movimiento
            BigDecimal nuevoStock = producto.getStockActual();

            switch (tipo) {
                case ENTRADA:
                    nuevoStock = nuevoStock.add(cantidad);
                    break;

                case SALIDA:
                case MERMA:
                    if (nuevoStock.compareTo(cantidad) < 0) {
                        throw new BusinessException(
                                "Stock insuficiente para '" + producto.getNombre() +
                                        "'. Disponible: " + nuevoStock + ", solicitado: " + cantidad);
                    }
                    nuevoStock = nuevoStock.subtract(cantidad);
                    break;

                case AJUSTE:
                    // El ajuste puede ser positivo (cantidad normal) o negativo (ver nota)
                    // Para simplificar: cantidad siempre positiva, y en el front se decide el signo.
                    // Aquí asumimos que AJUSTE resta si el observacion indica "faltante", o suma si "sobrante".
                    // Simplificación: AJUSTE resta stock (corrección por faltante en conteo físico).
                    if (nuevoStock.compareTo(cantidad) < 0) {
                        throw new BusinessException(
                                "El ajuste dejaría el stock en negativo para '" + producto.getNombre() + "'");
                    }
                    nuevoStock = nuevoStock.subtract(cantidad);
                    break;
            }

            producto.setStockActual(nuevoStock);
            productoRepository.save(producto);

            // Crear detalle
            DetalleMovimiento detalle = DetalleMovimiento.builder()
                    .movimiento(movimiento)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioCosto(detReq.getPrecioCosto() != null ? detReq.getPrecioCosto() : BigDecimal.ZERO)
                    .fechaVencimiento(detReq.getFechaVencimiento())
                    .build();

            movimiento.getDetalles().add(detalle);
        }

        Movimiento guardado = movimientoRepository.save(movimiento);
        return toResponse(guardado);
    }

    private MovimientoResponse toResponse(Movimiento m) {
        List<DetalleMovimientoResponse> detalles = m.getDetalles().stream()
                .map(d -> DetalleMovimientoResponse.builder()
                        .idDetalle(d.getIdDetalle())
                        .idProducto(d.getProducto().getIdProducto())
                        .nombreProducto(d.getProducto().getNombre())
                        .codigoSku(d.getProducto().getCodigoSku())
                        .cantidad(d.getCantidad())
                        .precioCosto(d.getPrecioCosto())
                        .fechaVencimiento(d.getFechaVencimiento())
                        .build())
                .collect(Collectors.toList());

        return MovimientoResponse.builder()
                .idMovimiento(m.getIdMovimiento())
                .tipoMovimiento(m.getTipoMovimiento().name())
                .fechaMovimiento(m.getFechaMovimiento())
                .observacion(m.getObservacion())
                .estado(m.getEstado().name())
                .idUsuario(m.getUsuario().getIdUsuario())
                .nombreUsuario(m.getUsuario().getNombreUsuario())
                .idProveedor(m.getProveedor() != null ? m.getProveedor().getIdProveedor() : null)
                .razonSocialProveedor(m.getProveedor() != null ? m.getProveedor().getRazonSocial() : null)
                .detalles(detalles)
                .build();
    }
}