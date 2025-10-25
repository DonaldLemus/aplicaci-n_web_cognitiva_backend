package com.umg.cognitiva.utilerias;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwTokenProvider {

    // Inyectas la clave Base64 desde properties
    @Value("${jwt.secret-base64}")
    private String secretBase64;

    // Al inicializar, decodificas el Base64 a un Key
    private Key key;

    @PostConstruct
    public void init() {
        byte[] decoded = Decoders.BASE64.decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(decoded);
    }

    private final long EXPIRATION_MS = 24 * 60 * 60 * 1000;

    public String generateToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_MS);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}