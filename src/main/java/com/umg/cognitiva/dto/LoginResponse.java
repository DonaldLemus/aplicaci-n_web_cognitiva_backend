package com.umg.cognitiva.dto;

import com.umg.cognitiva.model.Usuario;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UsuarioLoginResponse usuarioLoginResponse;

}
