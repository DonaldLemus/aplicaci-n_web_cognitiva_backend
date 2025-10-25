package com.umg.cognitiva.dto;

import lombok.Data;

@Data
public class UsuarioCorreoRequestDTO {
    private Long usuarioId;
    private String correo;
}
