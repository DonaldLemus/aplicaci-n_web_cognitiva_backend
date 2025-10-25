package com.umg.cognitiva.repository;

import com.umg.cognitiva.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT * FROM public.sp_login(:correo, :pwd)",
            nativeQuery = true)
    Optional<Usuario> spLogin(
            @Param("correo") String correo,
            @Param("pwd")    String password
    );
    /**
     * Invoca la función PL/pgSQL que definimos con CREATE FUNCTION y devuelve el ID.
     */
    @Query(value =
            "SELECT sp_registrar_usuario(" +
                    "  :p_nombre, " +
                    "  :p_correo, " +
                    "  :p_password, " +
                    "  :p_edad" +
                    ")", nativeQuery = true)
    Integer spRegistrarUsuario(
            @Param("p_nombre")   String nombre,
            @Param("p_correo")   String correo,
            @Param("p_password") String password,
            @Param("p_edad")     Integer edad
    );
}
