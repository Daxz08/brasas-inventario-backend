package com.brasas.Inventario.repository;

import com.brasas.Inventario.entity.CodigoVerificacion;
import com.brasas.Inventario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Integer> {
    Optional<CodigoVerificacion> findTopByUsuarioAndUsadoFalseOrderByFechaCreacionDesc(Usuario usuario);
}