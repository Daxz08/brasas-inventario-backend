package com.brasas.Inventario.repository;

import com.brasas.Inventario.entity.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Integer> {

    List<DetalleMovimiento> findByMovimientoIdMovimiento(Integer idMovimiento);

    List<DetalleMovimiento> findByProductoIdProducto(Integer idProducto);
}