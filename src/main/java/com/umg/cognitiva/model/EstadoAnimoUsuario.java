package com.umg.cognitiva.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "estado_animo_usuario")
@Data
public class EstadoAnimoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

}
