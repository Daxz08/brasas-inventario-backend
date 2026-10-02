package com.brasas.Inventario.config;

import com.brasas.Inventario.entity.Rol;
import com.brasas.Inventario.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            rolRepository.save(Rol.builder()
                    .nombreRol("Administrador")
                    .descripcion("Acceso total al sistema")
                    .build());

            rolRepository.save(Rol.builder()
                    .nombreRol("Empleado")
                    .descripcion("Acceso a consultas y registro de movimientos")
                    .build());
        }
    }
}