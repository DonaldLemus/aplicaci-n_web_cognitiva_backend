package com.umg.cognitiva.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RequestMapping("/api/v1")
@RestController
public class ConsejoSseController {

    private final CopyOnWriteArrayList<SseEmitter> clientes = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/consejos-sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamConsejos() {
        SseEmitter emitter = new SseEmitter(0L); // sin timeout
        clientes.add(emitter);

        emitter.onCompletion(() -> clientes.remove(emitter));
        emitter.onTimeout(() -> clientes.remove(emitter));
        emitter.onError((e) -> clientes.remove(emitter));

        return emitter;
    }

    public void enviarConsejoAClientes(String titulo, String descripcion) {
        for (SseEmitter emitter : clientes) {
            try {
                emitter.send(SseEmitter.event()
                        .name("nuevo-consejo")
                        .data(new ConsejoDTO(titulo, descripcion))
                );
            } catch (IOException e) {
                clientes.remove(emitter);
            }
        }
    }

    public record ConsejoDTO(String titulo, String descripcion) {}
}
