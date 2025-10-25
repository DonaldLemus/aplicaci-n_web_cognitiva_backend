package com.umg.cognitiva.scheduled;

import com.umg.cognitiva.controller.ConsejoSseController;
import com.umg.cognitiva.model.Consejo;
import com.umg.cognitiva.repository.ConsejoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class ConsejoScheduler {

    @Autowired
    private ConsejoRepository consejoRepository;

    @Autowired
    private ConsejoSseController sseController;

    private final Random random = new Random();

    @Scheduled(cron = "0 */1 * * * *")
    public void enviarConsejos() {
        List<Consejo> consejos = consejoRepository.findAll();
        if (!consejos.isEmpty()) {
            Consejo consejo = consejos.get(random.nextInt(consejos.size()));
            sseController.enviarConsejoAClientes(consejo.getTitulo(), consejo.getDescripcion());
        }
    }
}
