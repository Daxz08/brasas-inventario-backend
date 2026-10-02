package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.UsuarioRequest;
import com.brasas.Inventario.dto.response.UsuarioResponse;
import com.brasas.Inventario.entity.Rol;
import com.brasas.Inventario.entity.Usuario;
import com.brasas.Inventario.exception.BusinessException;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.RolRepository;
import com.brasas.Inventario.repository.UsuarioRepository;
import com.brasas.Inventario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        if (request.getContrasena() == null || request.getContrasena().isBlank()) {
            throw new BusinessException("La contraseña es obligatoria al crear un usuario");
        }
        if (usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new BusinessException("Ya existe un usuario con el nombre: " + request.getNombreUsuario());
        }
        if (request.getEmail() != null && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.getEmail());
        }

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.getIdRol()));

        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.getNombreUsuario())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .rol(rol)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if (!usuario.getNombreUsuario().equals(request.getNombreUsuario())
                && usuarioRepository.existsByNombreUsuario(request.getNombreUsuario())) {
            throw new BusinessException("Ya existe otro usuario con el nombre: " + request.getNombreUsuario());
        }

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.getIdRol()));

        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        if (request.getActivo() != null) usuario.setActivo(request.getActivo());

        // Solo actualiza la contraseña si viene con valor
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .idUsuario(u.getIdUsuario())
                .nombreUsuario(u.getNombreUsuario())
                .nombres(u.getNombres())
                .apellidos(u.getApellidos())
                .email(u.getEmail())
                .idRol(u.getRol().getIdRol())
                .nombreRol(u.getRol().getNombreRol())
                .activo(u.getActivo())
                .fechaCreacion(u.getFechaCreacion())
                .build();
    }
}