package com.brasas.Inventario.controller;

import com.brasas.Inventario.dto.request.LoginRequest;
import com.brasas.Inventario.dto.response.LoginResponse;
import com.brasas.Inventario.dto.response.UsuarioResponse;
import com.brasas.Inventario.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.obtenerUsuarioActual(authentication.getName()));
    }
}