package com.umg.cognitiva.repository;

import com.umg.cognitiva.model.InformacionMedica;
import com.umg.cognitiva.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InformacionMedicaRepository extends JpaRepository<InformacionMedica, Long> {

    Optional<InformacionMedica> findByUsuario(Usuario usuario);
}