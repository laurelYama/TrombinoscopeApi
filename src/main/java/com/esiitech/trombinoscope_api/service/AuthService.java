package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.DTOs.AuthRequest;
import com.esiitech.trombinoscope_api.DTOs.ChangerMotDePasseRequest;
import com.esiitech.trombinoscope_api.DTOs.AuthResponse;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Entity.PasswordResetToken;
import com.esiitech.trombinoscope_api.Enum.Role;
import com.esiitech.trombinoscope_api.Exception.ApiException;
import com.esiitech.trombinoscope_api.config.JwtTokenProvider;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import com.esiitech.trombinoscope_api.repository.PasswordResetTokenRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthResponse register(@Valid AuthRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Cet email est déjà utilisé.", HttpStatus.BAD_REQUEST);
        }

        Utilisateur newUser = Utilisateur.builder()
                .username(request.getEmail().split("@")[0])
                .email(request.getEmail())
                .password("")
                .role(request.getRole() != null ? request.getRole() : Role.NORMAL)
                .forcePasswordChange(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Utilisateur savedUser = utilisateurRepository.save(newUser);

        String generatedToken = UUID.randomUUID().toString();
        LocalDateTime expirationDate = LocalDateTime.now().plusHours(24);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(generatedToken)
                .utilisateur(savedUser)
                .expirationDate(expirationDate)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String frontendUrl = "http://localhost:4200/definir-mot-de-passe?token=" + generatedToken;

        emailService.sendSimpleMessage(
                savedUser.getEmail(),
                "Définissez votre mot de passe",
                "Bonjour " + savedUser.getUsername() + ",\n\n" +
                        "Votre compte a été créé avec succès.\n" +
                        "Veuillez cliquer sur le lien ci-dessous pour définir votre mot de passe :\n\n" +
                        frontendUrl + "\n\nCe lien expirera dans 24 heures."
        );

        return new AuthResponse(null, savedUser.getEmail(),
                "Inscription réussie. Un email vous a été envoyé pour définir votre mot de passe.",
                savedUser.getRole().name());
    }

    public AuthResponse authenticate(@Valid AuthRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé", HttpStatus.NOT_FOUND));

        if (user.isForcePasswordChange()) {
            throw new ApiException("Vous devez changer votre mot de passe avant de vous connecter", HttpStatus.FORBIDDEN);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return new AuthResponse(token, user.getEmail(), "Connexion réussie", user.getRole().name());
    }

}
