package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.ProveedorRequest;
import com.brasas.Inventario.dto.response.ProveedorResponse;
import com.brasas.Inventario.entity.Proveedor;
import com.brasas.Inventario.exception.BusinessException;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.ProveedorRepository;
import com.brasas.Inventario.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<ProveedorResponse> listarTodos() {
        return proveedorRepository.findByActivoTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProveedorResponse obtenerPorId(Integer id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        return toResponse(proveedor);
    }

    @Override
    public ProveedorResponse crear(ProveedorRequest request) {
        if (proveedorRepository.existsByRuc(request.getRuc())) {
            throw new BusinessException("Ya existe un proveedor con el RUC: " + request.getRuc());
        }

        Proveedor proveedor = Proveedor.builder()
                .ruc(request.getRuc())
                .razonSocial(request.getRazonSocial())
                .contacto(request.getContacto())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .activo(true)
                .build();

        return toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    public ProveedorResponse actualizar(Integer id, ProveedorRequest request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (!proveedor.getRuc().equals(request.getRuc())
                && proveedorRepository.existsByRuc(request.getRuc())) {
            throw new BusinessException("Ya existe otro proveedor con el RUC: " + request.getRuc());
        }

        proveedor.setRuc(request.getRuc());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setDireccion(request.getDireccion());

        return toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    public void eliminar(Integer id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        // Soft delete
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    private ProveedorResponse toResponse(Proveedor p) {
        return ProveedorResponse.builder()
                .idProveedor(p.getIdProveedor())
                .ruc(p.getRuc())
                .razonSocial(p.getRazonSocial())
                .contacto(p.getContacto())
                .telefono(p.getTelefono())
                .direccion(p.getDireccion())
                .activo(p.getActivo())
                .fechaRegistro(p.getFechaRegistro())
                .build();
    }
}