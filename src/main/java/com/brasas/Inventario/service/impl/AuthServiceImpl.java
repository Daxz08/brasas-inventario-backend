package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.LoginRequest;
import com.brasas.Inventario.dto.response.LoginResponse;
import com.brasas.Inventario.dto.response.UsuarioResponse;
import com.brasas.Inventario.entity.Usuario;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.UsuarioRepository;
import com.brasas.Inventario.security.JwtService;
import com.brasas.Inventario.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        // Si las credenciales son inválidas, Spring lanza BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNombreUsuario(),
                        request.getContrasena())
        );

        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = jwtService.generarToken(
                usuario.getNombreUsuario(),
                usuario.getRol().getNombreRol()
        );

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .nombreUsuario(usuario.getNombreUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .rol(usuario.getRol().getNombreRol())
                .build();
    }

    @Override
    public UsuarioResponse obtenerUsuarioActual(String username) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombreUsuario(usuario.getNombreUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .email(usuario.getEmail())
                .idRol(usuario.getRol().getIdRol())
                .nombreRol(usuario.getRol().getNombreRol())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}