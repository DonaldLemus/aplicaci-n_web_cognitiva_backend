package com.umg.cognitiva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResultadosXUsuario {

    private String nombre;
    private int edad;
    private String nombreJuego;
    private int puntuacion;
    private LocalDateTime fechaRealizacion;
}
