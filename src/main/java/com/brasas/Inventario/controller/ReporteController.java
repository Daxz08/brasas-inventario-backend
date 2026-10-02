package com.brasas.Inventario.controller;

import com.brasas.Inventario.service.ReporteService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/stock/pdf")
    public ResponseEntity<InputStreamResource> stockPdf() {
        ByteArrayInputStream stream = reporteService.generarPdfStock();
        return buildResponse(stream, "reporte-stock.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/stock/excel")
    public ResponseEntity<InputStreamResource> stockExcel() {
        ByteArrayInputStream stream = reporteService.generarExcelStock();
        return buildResponse(stream, "reporte-stock.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @GetMapping("/movimientos/pdf")
    public ResponseEntity<InputStreamResource> movimientosPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        ByteArrayInputStream stream = reporteService.generarPdfMovimientos(desde, hasta);
        return buildResponse(stream, "reporte-movimientos.pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping("/movimientos/excel")
    public ResponseEntity<InputStreamResource> movimientosExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        ByteArrayInputStream stream = reporteService.generarExcelMovimientos(desde, hasta);
        return buildResponse(stream, "reporte-movimientos.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    private ResponseEntity<InputStreamResource> buildResponse(ByteArrayInputStream stream,
                                                              String filename,
                                                              MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(stream));
    }
}