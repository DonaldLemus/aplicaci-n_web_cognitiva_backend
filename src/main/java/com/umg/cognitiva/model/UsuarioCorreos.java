package com.umg.cognitiva.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_correos_adicionales")
@Data
public class UsuarioCorreos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_usuario_id", referencedColumnName = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String correo;

    @CreationTimestamp
    @Column(name = "fecha_agregado", nullable = false, updatable = false)
    private LocalDateTime fechaAgregado;

    @Column(nullable = false)
    private Boolean activo = true;
}
