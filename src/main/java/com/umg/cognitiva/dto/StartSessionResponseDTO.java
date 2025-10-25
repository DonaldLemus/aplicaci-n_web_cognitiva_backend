package com.umg.cognitiva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartSessionResponseDTO {
    private Long sesionId;
    private String fechaInicio; // ISO string
}