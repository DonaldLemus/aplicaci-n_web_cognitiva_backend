package com.umg.cognitiva.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "persona_arbol")
@Data
public class PersonaArbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", referencedColumnName = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String parentesco;

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "id_padre")
    @JsonIgnore
    private PersonaArbol padre;

    @Column(name = "fecha_creacion")
    @JsonIgnore
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
