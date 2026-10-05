package com.brasas.Inventario.service.impl;

import com.brasas.Inventario.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void enviarCodigoVerificacion(String destinatario, String nombre, String codigo) {
        String url = "https://api.brevo.com/v3/smtp/email";

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", destinatario, "name", nombre)),
                "subject", "Codigo de verificacion - Brasas del Centro",
                "htmlContent",
                "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto'>" +
                        "<h2 style='color:#c1381a'>Brasas del Centro</h2>" +
                        "<p>Hola <strong>" + nombre + "</strong>,</p>" +
                        "<p>Tu codigo de verificacion es:</p>" +
                        "<p style='font-size:32px;font-weight:bold;letter-spacing:8px;color:#c1381a;text-align:center'>" + codigo + "</p>" +
                        "<p>Este codigo expira en 5 minutos.</p>" +
                        "<p style='font-size:12px;color:#888'>Si no solicitaste este codigo, ignora este mensaje.</p>" +
                        "</div>"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
}