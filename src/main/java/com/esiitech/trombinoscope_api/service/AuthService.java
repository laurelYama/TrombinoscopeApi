package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.DTOs.AuthRequest;
import com.esiitech.trombinoscope_api.Enum.Role;
import com.esiitech.trombinoscope_api.DTOs.AuthResponse;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.ApiException;
import com.esiitech.trombinoscope_api.config.JwtTokenProvider;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new ApiException("Cet email est déjà utilisé.", HttpStatus.BAD_REQUEST);
        }

        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword())); // Hash du mot de passe
        return utilisateurRepository.save(utilisateur);
    }

    public AuthResponse register(AuthRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Cet email est déjà utilisé.", HttpStatus.BAD_REQUEST);
        }

        Utilisateur newUser = Utilisateur.builder()
                .username(request.getEmail().split("@")[0])
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Maintenant encodé
                .role(request.getRole() != null ? request.getRole() : Role.NORMAL)
                .forcePasswordChange(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        newUser = creerUtilisateur(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(newUser, null, newUser.getAuthorities());
        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token, newUser.getEmail(), "Inscription réussie", newUser.getRole().name());
    }

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("Tentative de connexion avec email: " + request.getEmail());

        Object principal = authentication.getPrincipal();
        String email;
        String role;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
            Utilisateur user = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new ApiException("Utilisateur non trouvé", HttpStatus.NOT_FOUND));
            role = user.getRole().name();
        } else {
            throw new ApiException("Authentification invalide", HttpStatus.UNAUTHORIZED);
        }

        return new AuthResponse(token, email, "Connexion réussie", role);
    }
}
