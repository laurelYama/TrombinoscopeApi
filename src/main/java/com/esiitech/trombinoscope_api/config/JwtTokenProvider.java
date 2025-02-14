package com.esiitech.trombinoscope_api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "k0Nu6mj6r5Z1WNyhyH9XWLhvmrc88hmXKdwmXXHj2Sk="; // Change par une vraie clé secrète
    private static final long EXPIRATION_TIME = 86400000; // 1 jour en millisecondes

    // Générer un token JWT
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extraire l'email (username) du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Vérifier si le token est valide
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extraire les claims du token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // Vérifier si le token est expiré
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}

