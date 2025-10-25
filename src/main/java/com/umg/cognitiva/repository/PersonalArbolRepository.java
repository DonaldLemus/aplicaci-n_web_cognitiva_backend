package com.umg.cognitiva.repository;

import com.umg.cognitiva.model.PersonaArbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalArbolRepository extends JpaRepository<PersonaArbol, Long> {
    List<PersonaArbol> findByUsuarioId(Long idUsuario);
}
