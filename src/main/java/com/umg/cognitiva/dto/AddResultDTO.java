package com.umg.cognitiva.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddResultDTO {
    private Long idUsuario;
    private Long idActividad;
    private int puntuacion;
    private int tiempoTotal;
    private LocalDateTime fechaRealizacion;
}
