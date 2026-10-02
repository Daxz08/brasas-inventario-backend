package com.brasas.Inventario.repository;

import com.brasas.Inventario.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByCodigoSku(String codigoSku);

    boolean existsByCodigoSku(String codigoSku);

    List<Producto> findByActivoTrue();

    List<Producto> findByActivoTrueAndCategoriaIdCategoria(Integer idCategoria);

    List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual <= p.stockMinimo")
    List<Producto> findProductosConStockBajo();
}