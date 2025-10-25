package com.umg.cognitiva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndSessionResponseDTO {
    private Long sesionId;
    private int duracionTotal;  // segundos
    private String fechaFin;     // ISO string
}