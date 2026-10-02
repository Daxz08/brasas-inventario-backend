package com.brasas.Inventario.config;

import com.brasas.Inventario.entity.Rol;
import com.brasas.Inventario.entity.Usuario;
import com.brasas.Inventario.repository.RolRepository;
import com.brasas.Inventario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 1. Crear roles si no existen
        if (rolRepository.count() == 0) {
            rolRepository.save(Rol.builder()
                    .nombreRol("Administrador")
                    .descripcion("Acceso total al sistema")
                    .build());

            rolRepository.save(Rol.builder()
                    .nombreRol("Empleado")
                    .descripcion("Acceso a consultas y registro de movimientos")
                    .build());

            log.info("✅ Roles 'Administrador' y 'Empleado' creados.");
        }

        // 2. Crear usuario admin si no existe
        if (usuarioRepository.findByNombreUsuario("admin").isEmpty()) {

            Rol rolAdmin = rolRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Rol Administrador no encontrado"));

            Usuario admin = Usuario.builder()
                    .nombreUsuario("admin")
                    .contrasena(passwordEncoder.encode("admin123"))
                    .nombres("Administrador")
                    .apellidos("Sistema")
                    .email("admin@brasas.com")
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .rol(rolAdmin)
                    .build();

            usuarioRepository.save(admin);
            log.info("✅ Usuario admin creado: admin / admin123");
        }
    }
}