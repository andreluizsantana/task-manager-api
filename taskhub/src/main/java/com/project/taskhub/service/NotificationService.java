package com.project.taskhub.service;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class NotificationService {
    public void enviarAlerta(String mensagem, Exception e) {
	String detalhes = String.format("🚨 ALERTA CRÍTICO\n" + "Mensagem: %s\n" + "Exceção: %s\n" + "Timestamp: %s", mensagem, e.getClass().getSimpleName(), java.time.LocalDateTime.now());
	log.error(detalhes, e);
    }
}
