package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.dto.request.LoginRequest;
import com.brasas.Inventario.dto.response.LoginResponse;
import com.brasas.Inventario.dto.response.UsuarioResponse;
import com.brasas.Inventario.entity.CodigoVerificacion;
import com.brasas.Inventario.entity.Usuario;
import com.brasas.Inventario.exception.BusinessException;
import com.brasas.Inventario.exception.ResourceNotFoundException;
import com.brasas.Inventario.repository.CodigoVerificacionRepository;
import com.brasas.Inventario.repository.UsuarioRepository;
import com.brasas.Inventario.security.JwtService;
import com.brasas.Inventario.service.AuthService;
import com.brasas.Inventario.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final CodigoVerificacionRepository codigoRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public LoginResponse iniciarLogin(LoginRequest request) {
        // 1. Validar credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNombreUsuario(),
                        request.getContrasena())
        );

        Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 2. Validar que tenga correo registrado
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new BusinessException("El usuario no tiene correo registrado. Contacta al administrador.");
        }

        // 3. Generar código de 6 dígitos
        String codigo = String.format("%06d", new Random().nextInt(1000000));

        // 4. Guardar código en BD
        CodigoVerificacion cv = CodigoVerificacion.builder()
                .usuario(usuario)
                .codigo(codigo)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(5))
                .usado(false)
                .build();
        codigoRepository.save(cv);

        // 5. Enviar código por correo
        emailService.enviarCodigoVerificacion(
                usuario.getEmail(),
                usuario.getNombres(),
                codigo
        );

        // 6. Devolver respuesta sin token (aún no autenticado)
        return LoginResponse.builder()
                .requiere2FA(true)
                .nombreUsuario(usuario.getNombreUsuario())
                .mensaje("Se envió un código de verificación a " + maskEmail(usuario.getEmail()))
                .build();
    }

    @Override
    @Transactional
    public LoginResponse verificarCodigo(String nombreUsuario, String codigo) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        CodigoVerificacion cv = codigoRepository
                .findTopByUsuarioAndUsadoFalseOrderByFechaCreacionDesc(usuario)
                .orElseThrow(() -> new BusinessException("No hay código pendiente para este usuario"));

        // Validar expiración
        if (cv.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El código ha expirado. Solicita uno nuevo.");
        }

        // Validar código
        if (!cv.getCodigo().equals(codigo)) {
            throw new BusinessException("Código incorrecto");
        }

        // Marcar como usado
        cv.setUsado(true);
        codigoRepository.save(cv);

        // Generar token JWT final
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
                .requiere2FA(false)
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

    private String maskEmail(String email) {
        int at = email.indexOf("@");
        if (at <= 2) return email;
        return email.substring(0, 2) + "***" + email.substring(at);
    }
}