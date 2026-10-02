package com.brasas.Inventario.repository;

import com.brasas.Inventario.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    List<Movimiento> findByEstadoOrderByFechaMovimientoDesc(Movimiento.EstadoMovimiento estado);

    List<Movimiento> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
            LocalDateTime desde, LocalDateTime hasta);

    List<Movimiento> findByTipoMovimientoAndEstadoOrderByFechaMovimientoDesc(
            Movimiento.TipoMovimiento tipo, Movimiento.EstadoMovimiento estado);
}