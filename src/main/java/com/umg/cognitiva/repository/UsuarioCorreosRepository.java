package com.umg.cognitiva.repository;

import com.umg.cognitiva.model.Usuario;
import com.umg.cognitiva.model.UsuarioCorreos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioCorreosRepository extends JpaRepository<UsuarioCorreos, Long> {
    List<UsuarioCorreos> findByUsuario(Usuario usuario);
    @Query("SELECT u FROM UsuarioCorreos u WHERE u.usuario.id = :usuarioId")
    List<UsuarioCorreos> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
