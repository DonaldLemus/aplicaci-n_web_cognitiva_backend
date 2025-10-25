package com.umg.cognitiva.repository;

import com.umg.cognitiva.model.EstadoAnimoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstadoAnimoUsuarioRepository extends JpaRepository<EstadoAnimoUsuario, Long> {
    List<EstadoAnimoUsuario> findByUsuarioId(Long usuarioId);
}
