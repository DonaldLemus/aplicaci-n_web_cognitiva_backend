package com.umg.cognitiva.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InformacionMedicaDTO {

    private Long usuarioId;

    // Sección 1
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private String numeroIdentificacion;
    private String direccion;
    private String telefonoFijo;
    private String telefonoMovil;
    private String contactoNombre;
    private String contactoParentesco;
    private String contactoTelefono;
    private String medicoNombre;
    private String medicoTelefono;

    // Sección 2
    private String enfermedadesCronicas;
    private String medicamentos;
    private String alergiasAlimentarias;
    private String alergiasAmbientales;
    private boolean usaLentes;
    private boolean usaAudifono;
    private boolean usaProtesisDentadura;
    private String dispositivosMovilidad;

    // Sección 3
    private String dietaEspecial;
    private String nivelMovilidad;
    private String riesgoCaidas;
    private String rutinaEjercicio;
    private boolean puedeDucharseSolo;
    private boolean seVisteSolo;

}