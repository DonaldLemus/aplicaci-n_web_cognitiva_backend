package com.umg.cognitiva.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "actividades")
@AllArgsConstructor
@NoArgsConstructor
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actividad_id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "ruta", nullable = false, length = 100)
    private String ruta;
}