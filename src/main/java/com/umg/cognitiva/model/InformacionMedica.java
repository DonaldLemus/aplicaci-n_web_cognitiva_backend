package com.umg.cognitiva.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "informacion_medica")
@Data
public class InformacionMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    // Sección 1: Datos Generales
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "numero_identificacion", length = 50)
    private String numeroIdentificacion;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "telefono_fijo", length = 20)
    private String telefonoFijo;

    @Column(name = "telefono_movil", length = 20)
    private String telefonoMovil;

    // Contacto principal
    @Column(name = "contacto_nombre", length = 100)
    private String contactoNombre;

    @Column(name = "contacto_parentesco", length = 50)
    private String contactoParentesco;

    @Column(name = "contacto_telefono", length = 20)
    private String contactoTelefono;

    // Médico de cabecera
    @Column(name = "medico_nombre", length = 100)
    private String medicoNombre;

    @Column(name = "medico_telefono", length = 20)
    private String medicoTelefono;

    // Sección 2: Información Médica (guardado como texto separado por comas)
    @Column(name = "enfermedades_cronicas", columnDefinition = "TEXT")
    private String enfermedadesCronicas;

    @Column(columnDefinition = "TEXT")
    private String medicamentos;

    @Column(name = "alergias_alimentarias", columnDefinition = "TEXT")
    private String alergiasAlimentarias;

    @Column(name = "alergias_ambientales", columnDefinition = "TEXT")
    private String alergiasAmbientales;

    @Column(name = "usa_lentes")
    private Boolean usaLentes = false;

    @Column(name = "usa_audifono")
    private Boolean usaAudifono = false;

    @Column(name = "usa_protesis_dentadura")
    private Boolean usaProtesisDentadura = false;

    @Column(name = "dispositivos_movilidad", length = 100)
    private String dispositivosMovilidad;

    // Sección 3: Rutina Diaria
    @Column(name = "dieta_especial", length = 200)
    private String dietaEspecial;

    @Column(name = "nivel_movilidad", length = 50)
    private String nivelMovilidad;

    @Column(name = "riesgo_caidas", length = 20)
    private String riesgoCaidas;

    @Column(name = "rutina_ejercicio", columnDefinition = "TEXT")
    private String rutinaEjercicio;

    @Column(name = "puede_ducharse_solo")
    private Boolean puedeDucharseSolo = true;

    @Column(name = "se_viste_solo")
    private Boolean seVisteSolo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}