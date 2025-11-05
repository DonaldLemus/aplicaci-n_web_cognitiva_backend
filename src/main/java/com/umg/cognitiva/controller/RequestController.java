package com.umg.cognitiva.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.umg.cognitiva.dto.*;
import com.umg.cognitiva.model.*;
import com.umg.cognitiva.services.CognitivaServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class RequestController {

    @Autowired
    private CognitivaServices cognitivaServices;

    @Qualifier("conversionService")

    @PostMapping("/addUsuario")
    public ResponseEntity<?> addUsuario(
            @Valid @RequestBody AddUserDTO dto,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            String errores = bindingResult.getFieldErrors()
                    .stream()
                    .map(f -> f.getField() + ": " + f.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            response.put("estado", "error");
            response.put("mensaje", "Datos inválidos: " + errores);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        try {
            boolean resultado = cognitivaServices.crearUsuario(
                    dto
            );
            if (resultado){
                response.put("status", true);
                response.put("mensaje", "Usuario creado exitosamente");
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(response);
            } else {
                response.put("status", false);
                response.put("mensaje", "No fue posible crear el usuario");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(response);
            }


        } catch (Exception e) {
            response.put("estado", "error");
            response.put("mensaje", "Error interno al crear usuario");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // Endpoint para actualizar un usuario existente
    @PutMapping("/editar/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable("id") Long id, @RequestBody Usuario usuarioActualizado) {
        Optional<Usuario> usuario = cognitivaServices.actualizarInfoUsuario(id, usuarioActualizado);

        if (usuario.isPresent()) {
            return new ResponseEntity<>("Usuario actualizado con exito", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/actualizarPuntos")
    public ResponseEntity<?> actualizarPuntosUsuario(@RequestBody Map<String, Object> parametros) {
        Long id = ((Number) parametros.get("id")).longValue();
        Integer puntos = (Integer) parametros.get("puntos");

        Optional<Usuario> usuario = cognitivaServices.actualizarPuntos(id, puntos);
        return usuario.<ResponseEntity<?>>map(value -> new ResponseEntity<>("Puntos de usuario " + value.getNombre() + " actualizados con éxito", HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("Error al actualizar puntos", HttpStatus.BAD_REQUEST));
    }

    // Endpoint para obtener todas las actividades registradas
    @GetMapping("/listarActividades")
    public ResponseEntity<?> listarActividades() {
        try {
            List<Actividad> actividades = cognitivaServices.obtenerActividades();
            return new ResponseEntity<>(actividades, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listarFamiliares/{usuarioId}")
    public ResponseEntity<?> listarFamiliares(@PathVariable Long usuarioId){
        try {
            List<PersonaArbol> familiares = cognitivaServices.obtenerPersonas(usuarioId);
            if(familiares!=null && !familiares.isEmpty()){
                return new ResponseEntity<>(familiares, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/registrarSesion")
    public ResponseEntity<?> addSesion(@RequestBody SesionDTO sesionDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean b = cognitivaServices.registrarSesion(sesionDTO);
            if (b) {
                response.put("estado", "exito");
            } else {
                response.put("estado", "error");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("estado", "error");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/registrarResultado")
    public ResponseEntity<?> addResultado(@RequestBody AddResultDTO resultDTO) {
        System.out.println("Ejecutando actualizacion "+resultDTO);
        Map<String, String> response = new HashMap<>();
        try {
            boolean b = cognitivaServices.registrarResultado(resultDTO);
            if (b) {
                response.put("estado", "exito");
                return ResponseEntity.ok(response);
            } else {
                response.put("estado", "error");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("estado", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/startSession")
    public ResponseEntity<StartSessionResponseDTO> start(
            @RequestBody StartSessionDTO dto) {
        StartSessionResponseDTO resp = cognitivaServices.iniciarSesion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/{id}/endSession")
    public ResponseEntity<EndSessionResponseDTO> end(
            @PathVariable("id") Long sesionId) {
        EndSessionResponseDTO resp = cognitivaServices.finalizarSesion(sesionId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/registrarEstadoAnimo")
    public ResponseEntity<?> registrarEstadoAnimo(@RequestBody EstadoAnimoDTO dto) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean b = cognitivaServices.registrarEstadoAnimo(dto);
            if (b) {
                response.put("estado", "exito");
            } else {
                response.put("estado", "error");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("estado", "error");
            return (ResponseEntity<?>) ResponseEntity.badRequest  ();
        }
    }

    @GetMapping("/descargar/{usuarioId}")
    public ResponseEntity<?> descargarReporte(@PathVariable Long usuarioId) throws IOException, DocumentException {
        try {
            byte[] pdf = cognitivaServices.generateEstadoAnimoPdf(usuarioId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition
                    .attachment()
                    .filename("reporte_estado_animo.pdf")
                    .build());

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar pdf");
        }
    }

    @GetMapping("/enviar-reporte/{usuarioId}")
    public ResponseEntity<?> enviarReportePorCorreo(@PathVariable Long usuarioId) {
        Map<String, Object> response = new HashMap<>();
        try {

            boolean enviado = cognitivaServices.enviarReporte(usuarioId);
            if (enviado) {
                response.put("estado", "exito");
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("estado", "error");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo.");
            }
        } catch (Exception e) {
            response.put("estado", "error");
            response.put("status", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/agregarPersona", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> agregarPersona(
            @RequestPart("persona") String personaJson,
            @RequestPart(value = "foto") MultipartFile foto
    ) {
        try {
            PersonaArbolDTO personaDTO = new ObjectMapper().readValue(personaJson, PersonaArbolDTO.class);
            PersonaArbol persona = cognitivaServices.agregarPersona(personaDTO, foto);
            return ResponseEntity.ok(persona);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/agregarFoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> agregarFoto(
            @RequestPart("foto") MultipartFile foto,
            @RequestPart("dto") Map<String, Object> dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long idUsuario = Long.valueOf(dto.get("idUsuario").toString());
            String url = cognitivaServices.actualizarUrlFoto(foto, idUsuario);

            if (url != null && !url.isEmpty()) {
                response.put("message", url);
                response.put("status", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("estado", "error");
                response.put("status", false);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/registrarCorreo")
    public ResponseEntity<?> agregarCorreo(@RequestBody UsuarioCorreoRequestDTO dto){
        Map<String, String> response = new HashMap<>();
        try {
            UsuarioCorreos correo = cognitivaServices.agregarCorreoAdicional(dto);
            if (correo != null) {
                response.put("estado", "exito");
                response.put("correo", correo.getCorreo());
                return ResponseEntity.ok(response);
            }else {
                response.put("estado", "error");
                response.put("correo", "error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("estado", e.getMessage()));
        }
    }

    @PostMapping("/registrarInformacionMedica")
    public ResponseEntity<?> registrar(@RequestBody InformacionMedicaDTO dto) {
        try {
            InformacionMedica info = cognitivaServices.guardarInformacion(dto);
            return ResponseEntity.ok(info);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}