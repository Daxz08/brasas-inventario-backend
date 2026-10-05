package com.brasas.Inventario.controller;

import com.brasas.Inventario.dto.request.LoginRequest;
import com.brasas.Inventario.dto.response.LoginResponse;
import com.brasas.Inventario.dto.response.UsuarioResponse;
import com.brasas.Inventario.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.iniciarLogin(request));  // ← iniciarLogin, no login
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<LoginResponse> verificarCodigo(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.verificarCodigo(
                body.get("nombreUsuario"),
                body.get("codigo")
        ));
    }
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.obtenerUsuarioActual(authentication.getName()));
    }
}