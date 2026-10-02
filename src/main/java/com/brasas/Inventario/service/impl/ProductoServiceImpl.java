package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.ProductoRequest;
import com.brasas.Inventario.dto.response.ProductoResponse;
import com.brasas.Inventario.entity.Categoria;
import com.brasas.Inventario.entity.Producto;
import com.brasas.Inventario.exception.BusinessException;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.CategoriaRepository;
import com.brasas.Inventario.repository.ProductoRepository;
import com.brasas.Inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponse> listarPorCategoria(Integer idCategoria) {
        return productoRepository.findByActivoTrueAndCategoriaIdCategoria(idCategoria).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponse obtenerPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return toResponse(producto);
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByCodigoSku(request.getCodigoSku())) {
            throw new BusinessException("Ya existe un producto con el SKU: " + request.getCodigoSku());
        }

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + request.getIdCategoria()));

        Producto producto = Producto.builder()
                .codigoSku(request.getCodigoSku())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .unidadMedida(request.getUnidadMedida())
                .stockActual(request.getStockActual())
                .stockMinimo(request.getStockMinimo())
                .precioUnitario(request.getPrecioUnitario())
                .categoria(categoria)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        if (!producto.getCodigoSku().equals(request.getCodigoSku())
                && productoRepository.existsByCodigoSku(request.getCodigoSku())) {
            throw new BusinessException("Ya existe otro producto con el SKU: " + request.getCodigoSku());
        }

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con ID: " + request.getIdCategoria()));

        producto.setCodigoSku(request.getCodigoSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setStockActual(request.getStockActual());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setPrecioUnitario(request.getPrecioUnitario());
        producto.setCategoria(categoria);
        if (request.getActivo() != null) producto.setActivo(request.getActivo());

        return toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoResponse toResponse(Producto p) {
        return ProductoResponse.builder()
                .idProducto(p.getIdProducto())
                .codigoSku(p.getCodigoSku())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .unidadMedida(p.getUnidadMedida())
                .stockActual(p.getStockActual())
                .stockMinimo(p.getStockMinimo())
                .precioUnitario(p.getPrecioUnitario())
                .idCategoria(p.getCategoria().getIdCategoria())
                .nombreCategoria(p.getCategoria().getNombre())
                .activo(p.getActivo())
                .fechaRegistro(p.getFechaRegistro())
                .fechaModificacion(p.getFechaModificacion())
                .build();
    }
}