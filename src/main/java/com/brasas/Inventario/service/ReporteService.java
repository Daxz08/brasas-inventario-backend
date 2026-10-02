package com.brasas.Inventario.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

public interface ReporteService {

    ByteArrayInputStream generarPdfStock();

    ByteArrayInputStream generarExcelStock();

    ByteArrayInputStream generarPdfMovimientos(LocalDate desde, LocalDate hasta);

    ByteArrayInputStream generarExcelMovimientos(LocalDate desde, LocalDate hasta);
}