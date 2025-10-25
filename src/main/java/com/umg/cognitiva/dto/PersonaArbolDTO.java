package com.umg.cognitiva.dto;

import lombok.Data;

@Data
public class PersonaArbolDTO {
    private Long idUsuario;
    private String nombre;
    private String parentesco;
    private Long idPadre; // puede ser null
}