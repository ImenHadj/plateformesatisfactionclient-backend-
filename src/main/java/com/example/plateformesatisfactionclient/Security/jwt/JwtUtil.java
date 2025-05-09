package com.example.plateformesatisfactionclient.Security.jwt;

import com.example.plateformesatisfactionclient.Entity.User;
import com.example.plateformesatisfactionclient.Repository.UserRepository;
import com.example.plateformesatisfactionclient.Security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Autowired
    private UserRepository userRepository;
    private final SecretKey key;

    @Value("${jwt.expirationMs}")
    private long expirationMs;


    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .claim("sub", username)  // Remplace `subject()` par `claim("sub", username)`
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, Jwts.SIG.HS256) // Utilise `Jwts.SIG.HS256` pour la signature
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key) // `verifyWith` fonctionne maintenant correctement
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("sub", String.class); // Récupère `sub` au lieu de `subject()`
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }




}