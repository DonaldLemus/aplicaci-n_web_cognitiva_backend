package com.umg.cognitiva.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consejo")
public class Consejo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consejo")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "descripcion", nullable = false, columnDefinition = "text")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false, insertable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;
}
