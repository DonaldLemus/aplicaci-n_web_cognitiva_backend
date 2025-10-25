package com.umg.cognitiva.repository;

import com.umg.cognitiva.dto.ResultadosXUsuario;
import com.umg.cognitiva.model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    @Query("SELECT new com.umg.cognitiva.dto.ResultadosXUsuario(" +
            "u.nombre, u.edad, a.nombre, r.puntuacion, r.fechaRealizacion) " +
            "FROM Usuario u " +
            "JOIN Resultado r ON u.id = r.usuario.id " +
            "JOIN Actividad a ON r.actividad.id = a.id " +
            "WHERE u.id = :id")
    List<ResultadosXUsuario> findResultadosPorUsuario(@Param("id") Long usuarioId);

}
