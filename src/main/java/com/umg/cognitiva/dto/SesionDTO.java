package com.umg.cognitiva.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SesionDTO {

    private Long idUsuario;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int duracionTotal;
}
