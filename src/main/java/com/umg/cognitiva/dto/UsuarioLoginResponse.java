package com.umg.cognitiva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLoginResponse {
    private Long id;
    private String nombre;
    private String correo;
    private int total_puntos;
}
