package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.entity.Movimiento;
import com.brasas.Inventario.entity.Producto;
import com.brasas.Inventario.repository.MovimientoRepository;
import com.brasas.Inventario.repository.ProductoRepository;
import com.brasas.Inventario.service.ReporteService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ==================== PDF STOCK ====================
    @Override
    public ByteArrayInputStream generarPdfStock() {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Stock Actual", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Pollería Brasas del Centro S.A.C. - " +
                    LocalDateTime.now().format(FMT_FECHA));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            document.add(subtitulo);

            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 4, 3, 2, 2, 2});

            String[] headers = {"SKU", "Producto", "Categoría", "Stock", "Mínimo", "Estado"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 10, Font.BOLD)));
                cell.setBackgroundColor(new Color(230, 230, 230));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(cell);
            }

            List<Producto> productos = productoRepository.findByActivoTrue();
            for (Producto p : productos) {
                tabla.addCell(p.getCodigoSku());
                tabla.addCell(p.getNombre());
                tabla.addCell(p.getCategoria().getNombre());
                tabla.addCell(p.getStockActual().toString());
                tabla.addCell(p.getStockMinimo().toString());
                String estado = p.getStockActual().compareTo(p.getStockMinimo()) <= 0 ? "BAJO" : "NORMAL";
                tabla.addCell(estado);
            }

            document.add(tabla);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ==================== EXCEL STOCK ====================
    @Override
    public ByteArrayInputStream generarExcelStock() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Stock Actual");

            Row header = sheet.createRow(0);
            String[] cols = {"SKU", "Producto", "Categoría", "Unidad", "Stock", "Mínimo", "Precio", "Estado"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            List<Producto> productos = productoRepository.findByActivoTrue();
            int rowIdx = 1;
            for (Producto p : productos) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getCodigoSku());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getCategoria().getNombre());
                row.createCell(3).setCellValue(p.getUnidadMedida());
                row.createCell(4).setCellValue(p.getStockActual().doubleValue());
                row.createCell(5).setCellValue(p.getStockMinimo().doubleValue());
                row.createCell(6).setCellValue(p.getPrecioUnitario().doubleValue());
                row.createCell(7).setCellValue(
                        p.getStockActual().compareTo(p.getStockMinimo()) <= 0 ? "BAJO" : "NORMAL");
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage());
        }
    }

    // ==================== PDF MOVIMIENTOS ====================
    @Override
    public ByteArrayInputStream generarPdfMovimientos(LocalDate desde, LocalDate hasta) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Movimientos", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Pollería Brasas del Centro S.A.C. - Del " +
                    desde + " al " + hasta);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            document.add(subtitulo);

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 3, 2, 2, 2, 2, 3});

            String[] headers = {"Fecha", "Producto", "Tipo", "Cantidad", "Costo", "Usuario", "Observación"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, new Font(Font.HELVETICA, 10, Font.BOLD)));
                cell.setBackgroundColor(new Color(230, 230, 230));
                tabla.addCell(cell);
            }

            LocalDateTime desdeDt = desde.atStartOfDay();
            LocalDateTime hastaDt = hasta.atTime(LocalTime.MAX);
            List<Movimiento> movimientos = movimientoRepository
                    .findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(desdeDt, hastaDt);

            for (Movimiento m : movimientos) {
                if (m.getEstado() != Movimiento.EstadoMovimiento.CONFIRMADO) continue;
                m.getDetalles().forEach(d -> {
                    tabla.addCell(m.getFechaMovimiento().format(FMT_FECHA));
                    tabla.addCell(d.getProducto().getNombre());
                    tabla.addCell(m.getTipoMovimiento().name());
                    tabla.addCell(d.getCantidad().toString());
                    tabla.addCell(d.getPrecioCosto().toString());
                    tabla.addCell(m.getUsuario().getNombreUsuario());
                    tabla.addCell(m.getObservacion() != null ? m.getObservacion() : "-");
                });
            }

            document.add(tabla);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ==================== EXCEL MOVIMIENTOS ====================
    @Override
    public ByteArrayInputStream generarExcelMovimientos(LocalDate desde, LocalDate hasta) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Movimientos");

            Row header = sheet.createRow(0);
            String[] cols = {"Fecha", "Producto", "SKU", "Tipo", "Cantidad", "Costo", "Usuario", "Observación"};
            for (int i = 0; i < cols.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            LocalDateTime desdeDt = desde.atStartOfDay();
            LocalDateTime hastaDt = hasta.atTime(LocalTime.MAX);
            List<Movimiento> movimientos = movimientoRepository
                    .findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(desdeDt, hastaDt);

            int rowIdx = 1;
            for (Movimiento m : movimientos) {
                if (m.getEstado() != Movimiento.EstadoMovimiento.CONFIRMADO) continue;
                for (var d : m.getDetalles()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(m.getFechaMovimiento().format(FMT_FECHA));
                    row.createCell(1).setCellValue(d.getProducto().getNombre());
                    row.createCell(2).setCellValue(d.getProducto().getCodigoSku());
                    row.createCell(3).setCellValue(m.getTipoMovimiento().name());
                    row.createCell(4).setCellValue(d.getCantidad().doubleValue());
                    row.createCell(5).setCellValue(d.getPrecioCosto().doubleValue());
                    row.createCell(6).setCellValue(m.getUsuario().getNombreUsuario());
                    row.createCell(7).setCellValue(m.getObservacion() != null ? m.getObservacion() : "-");
                }
            }

            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage());
        }
    }
}