package com.umg.cognitiva.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resultados")
@AllArgsConstructor
@NoArgsConstructor
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resultado_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    @Column(name = "puntuacion", nullable = false)
    private int puntuacion;

    @Column(name = "tiempo_total")
    private int tiempoTotal; // Tiempo en segundos

    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDateTime fechaRealizacion;
}
