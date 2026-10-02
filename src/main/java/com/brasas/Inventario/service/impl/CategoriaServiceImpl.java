package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.CategoriaRequest;
import com.brasas.Inventario.dto.response.CategoriaResponse;
import com.brasas.Inventario.entity.Categoria;
import com.brasas.Inventario.exception.BusinessException;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.CategoriaRepository;
import com.brasas.Inventario.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    public List<CategoriaResponse> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaResponse obtenerPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return toResponse(categoria);
    }

    @Override
    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe una categoría con el nombre: " + request.getNombre());
        }

        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        return toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponse actualizar(Integer id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (!categoria.getNombre().equals(request.getNombre())
                && categoriaRepository.existsByNombre(request.getNombre())) {
            throw new BusinessException("Ya existe otra categoría con el nombre: " + request.getNombre());
        }

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        return toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminar(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        categoriaRepository.delete(categoria);
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}