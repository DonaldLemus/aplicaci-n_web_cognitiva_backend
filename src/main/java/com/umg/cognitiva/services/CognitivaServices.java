package com.umg.cognitiva.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.umg.cognitiva.dto.*;
import com.umg.cognitiva.model.*;
import com.umg.cognitiva.repository.*;
import com.umg.cognitiva.utilerias.JwTokenProvider;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CognitivaServices {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwTokenProvider jwTokenProvider;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private ResultadoRepository resultadoRepository;

    @Autowired
    private EstadoAnimoUsuarioRepository estadoAnimoUsuarioRepository;

    @Autowired
    private UsuarioCorreosRepository usuarioCorreosRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private PersonalArbolRepository personalArbolRepository;

    // Método para actualizar un usuario existente
    public Optional<Usuario> actualizarInfoUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = Optional.ofNullable(usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("USUARIO NO ENCONTRADO")));

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setCorreo(usuarioActualizado.getCorreo());
            //usuario.setPassword(usuarioActualizado.getPassword());
            usuario.setEdad(usuarioActualizado.getEdad());
            //usuario.setTotal_puntos(usuarioActualizado.getTotal_puntos());
            return Optional.of(usuarioRepository.save(usuario));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Usuario> actualizarPuntos(Long id, int puntos) {
        Optional<Usuario> usuarioExistente = Optional.ofNullable(usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("USUARIO NO ENCONTRADO")));
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setTotal_puntos(calculoPuntos(puntos, usuario.getTotal_puntos()));
            return Optional.of(usuarioRepository.save(usuario));
        } else {
            return Optional.empty();
        }
    }

    private Integer calculoPuntos(int puntos, int puntosActuales){
        int puntosFinal = 0;
        puntosFinal = puntos + puntosActuales;
        return puntosFinal;
    }

    public LoginResponse login(String email, String password) {
        LoginResponse loginResponse = new LoginResponse();
        Optional usuario = usuarioRepository.spLogin(email, password);
        if (usuario.isPresent()) {
            Usuario u = (Usuario) usuario.get();
            String token = jwTokenProvider.generateToken(email);
            UsuarioLoginResponse usuarioLoginResponse = new UsuarioLoginResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getTotal_puntos());
            loginResponse.setToken(token);
            loginResponse.setUsuarioLoginResponse(usuarioLoginResponse);
        } else {
            loginResponse = null;
        }
        return loginResponse;
    }

    /**
     * Llama al SP via repository y devuelve true si retorna un ID válido.
     */
    public boolean crearUsuario(AddUserDTO usuario) {
        try {
            Integer newId = usuarioRepository.spRegistrarUsuario(
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    usuario.getPassword(),
                    usuario.getEdad()
            );
            return newId != null;
        } catch (Exception e) {
            // loguea e si lo deseas
            return false;
        }
    }

    // Método para obtener todas las actividades
    public List<Actividad> obtenerActividades() {
        return actividadRepository.findAll();
    }

    public List<PersonaArbol> obtenerPersonas(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return personalArbolRepository.findByUsuarioId(id);
    }

    public boolean registrarResultado(AddResultDTO addResultDTO){

        try {
            Usuario usuario = usuarioRepository.findById(addResultDTO.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Actividad actividad = actividadRepository.findById(addResultDTO.getIdActividad()).orElseThrow(() -> new RuntimeException("Actividad no encontrado"));

            Resultado nuevoResultado = new Resultado();
            nuevoResultado.setUsuario(usuario);
            nuevoResultado.setActividad(actividad);
            nuevoResultado.setPuntuacion(addResultDTO.getPuntuacion());
            nuevoResultado.setTiempoTotal(addResultDTO.getTiempoTotal());
            nuevoResultado.setFechaRealizacion(addResultDTO.getFechaRealizacion());

            resultadoRepository.save(nuevoResultado);
            return true;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    // Método para registrar una nueva sesión
    public boolean registrarSesion(SesionDTO sesionDTO) {
        try {
            Usuario usuarioExistente = usuarioRepository.findById(sesionDTO.getIdUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Sesion nuevaSesion = new Sesion();
            nuevaSesion.setUsuario(usuarioExistente);
            nuevaSesion.setFechaInicio(sesionDTO.getFechaInicio());
            nuevaSesion.setFechaFin(sesionDTO.getFechaFin());
            nuevaSesion.setDuracionTotal(sesionDTO.getDuracionTotal());

            sesionRepository.save(nuevaSesion);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public List<ResultadosXUsuario> obtenerResultadosUsuario(Long id){

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<ResultadosXUsuario> resultados = resultadoRepository.findResultadosPorUsuario(id);
        return resultados;

    }

    /** Crea la sesión y devuelve el ID y la fecha de inicio */
    public StartSessionResponseDTO iniciarSesion(StartSessionDTO dto) {
        Usuario u = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Sesion sesion = new Sesion();
        sesion.setUsuario(u);
        sesion.setFechaInicio(LocalDateTime.now());
        // fechaFin y duracion_total quedan null/0
        sesion = sesionRepository.save(sesion);

        return new StartSessionResponseDTO(
                sesion.getId(),
                sesion.getFechaInicio().toString()
        );
    }

    /** Cierra la sesión abierta y calcula la duración */
    public EndSessionResponseDTO finalizarSesion(Long sesionId) {
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        LocalDateTime fin = LocalDateTime.now();
        sesion.setFechaFin(fin);
        int segundos = (int) Duration.between(sesion.getFechaInicio(), fin).getSeconds();
        sesion.setDuracionTotal(segundos);

        sesionRepository.save(sesion);

        return new EndSessionResponseDTO(
                sesion.getId(),
                segundos,
                sesion.getFechaFin().toString()
        );
    }

    public boolean registrarEstadoAnimo(EstadoAnimoDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        EstadoAnimoUsuario estado = new EstadoAnimoUsuario();
        estado.setUsuario(usuarioOpt.get());
        estado.setEstado(dto.getEstado());
        estado.setDescripcion(dto.getDescripcion());

        estadoAnimoUsuarioRepository.save(estado);
        return true;
    }

    public UsuarioCorreos agregarCorreoAdicional(UsuarioCorreoRequestDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        UsuarioCorreos correo = new UsuarioCorreos();
        correo.setUsuario(usuario);
        correo.setCorreo(dto.getCorreo());
        correo.setActivo(true);

        return usuarioCorreosRepository.save(correo);
    }


    public boolean enviarReporte(Long usuario){

        try {
            byte[] pdf = generateEstadoAnimoPdf(usuario);
            enviarEmail(usuario, pdf);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public void enviarEmail(Long usuarioId, byte[] baos) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<EstadoAnimoUsuario> estados = estadoAnimoUsuarioRepository.findByUsuarioId(usuarioId);

        if (estados.isEmpty()) {
            throw new Exception("El usuario no tiene estados de ánimo registrados");
        }

        // Obtener correos
        List<String> correos = new ArrayList<>();
        correos.add(usuario.getCorreo());

        List<UsuarioCorreos> adicionales = usuarioCorreosRepository.findByUsuarioId(usuarioId);
        for (UsuarioCorreos adicional : adicionales) {
            if (Boolean.TRUE.equals(adicional.getActivo())) {
                correos.add(adicional.getCorreo());
            }
        }
        
        // Enviar email
        for (String correo : correos) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(correo);
            helper.setSubject("Reporte de Estado de Ánimo");
            helper.setText("Adjunto encontrará su reporte de estados de ánimo recientes.");

            DataSource attachment = new ByteArrayDataSource(baos, "application/pdf");
            helper.addAttachment("reporte_estado_animo.pdf", attachment);

            mailSender.send(message);
        }
    }

    public byte[] generateEstadoAnimoPdf(Long usuarioId) throws IOException, DocumentException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<EstadoAnimoUsuario> estados = estadoAnimoUsuarioRepository.findByUsuarioId(usuarioId);
        List<ResultadosXUsuario> resultados = resultadoRepository.findResultadosPorUsuario(usuarioId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("Reporte de Estado de Ánimo del usuario: " + usuario.getNombre()));
        document.add(new Paragraph("Fecha de generación: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm:ss").withLocale(new Locale("es", "ES"))
        )));
        document.add(new Paragraph(" "));

        // Estado de ánimo
        if (estados.isEmpty()) {
            document.add(new Paragraph("El usuario no tiene registros de estado de ánimo."));
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' hh:mm a").withLocale(new Locale("es", "ES"));
            for (EstadoAnimoUsuario estado : estados) {
                String fechaFormateada = estado.getFecha().format(formatter);
                String descripcion = (estado.getDescripcion() != null && !estado.getDescripcion().isEmpty())
                        ? estado.getDescripcion()
                        : "N/A";
                document.add(new Paragraph("Fecha: " + fechaFormateada +
                        " | Estado: " + estado.getEstado() +
                        " | Descripción: " + descripcion));
            }
        }

        // SALTO DE PÁGINA
        document.newPage();
        document.add(new Paragraph("Reporte de Resultados por Actividad"));
        document.add(new Paragraph("Usuario: " + usuario.getNombre() + " | Edad: " + usuario.getEdad()));
        document.add(new Paragraph(" "));

        if (resultados.isEmpty()) {
            document.add(new Paragraph("NO HAY RESULTADOS."));
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withLocale(new Locale("es", "ES"));
            for (ResultadosXUsuario resultado : resultados) {
                String fecha = resultado.getFechaRealizacion().format(formatter);
                document.add(new Paragraph("Actividad: " + resultado.getNombreJuego() +
                        " | Puntuación: " + resultado.getPuntuacion() +
                        " | Fecha: " + fecha));
            }
        }

        document.close();
        return baos.toByteArray();
    }

    public String subirFotoYObtenerUrl(MultipartFile foto, Long idUsuario) throws IOException {
        String carpeta = "cognitiva/personas/" + idUsuario;
        System.out.println("subiendo foto");
        Map uploadResult = cloudinary.uploader().upload(foto.getBytes(), ObjectUtils.asMap(
                "folder", carpeta,
                "resource_type", "image"
        ));
        return (String) uploadResult.get("secure_url");
    }

    public PersonaArbol agregarPersona(PersonaArbolDTO dto, MultipartFile foto) throws IOException {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PersonaArbol persona = new PersonaArbol();
        persona.setUsuario(usuario);
        persona.setNombre(dto.getNombre());
        persona.setParentesco(dto.getParentesco());

        if (dto.getIdPadre() != null) {
            PersonaArbol padre = personalArbolRepository.findById(dto.getIdPadre())
                    .orElseThrow(() -> new RuntimeException("Persona padre no encontrada"));
            persona.setPadre(padre);
        }
        System.out.println("FOTO SI EXISTE ANTES DE IF nombre" + foto.getOriginalFilename());
        if (foto != null && !foto.isEmpty()) {
            System.out.println("FOTO SI EXISTE");
            String url = subirFotoYObtenerUrl(foto, dto.getIdUsuario()); // Reutilizamos esta lógica
            persona.setFotoUrl(url);
        }

        return personalArbolRepository.save(persona);
    }


}
