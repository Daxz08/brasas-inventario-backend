package com.brasas.Inventario.service;

public interface EmailService {
    void enviarCodigoVerificacion(String destinatario, String nombre, String codigo);
}