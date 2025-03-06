package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.DTOs.AuthRequest;
import com.esiitech.trombinoscope_api.DTOs.ChangerMotDePasseRequest;
import com.esiitech.trombinoscope_api.Enum.Role;
import com.esiitech.trombinoscope_api.DTOs.AuthResponse;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.ApiException;
import com.esiitech.trombinoscope_api.config.JwtTokenProvider;
import com.esiitech.trombinoscope_api.config.PasswordGenerator;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new ApiException("Cet email est déjà utilisé.", HttpStatus.BAD_REQUEST);
        }

        // Vérifier si le mot de passe est déjà encodé pour éviter la double encodage
        if (!utilisateur.getPassword().startsWith("$2a$")) {
            String rawPassword = utilisateur.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);

            utilisateur.setPassword(encodedPassword);
        }

        return utilisateurRepository.save(utilisateur);
    }




    public AuthResponse register(@Valid AuthRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Cet email est déjà utilisé.", HttpStatus.BAD_REQUEST);
        }

        // Générer un mot de passe sécurisé
        String generatedPassword = PasswordGenerator.generatePassword();
        String encodedPassword = passwordEncoder.encode(generatedPassword);

        Utilisateur newUser = Utilisateur.builder()
                .username(request.getEmail().split("@")[0])
                .email(request.getEmail())
                .password(encodedPassword) // Encodage unique
                .role(request.getRole() != null ? request.getRole() : Role.NORMAL)
                .forcePasswordChange(true) // Obligé de changer son mot de passe à la première connexion
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Utilisateur savedUser = creerUtilisateur(newUser);

        // Envoyer l'email avec le mot de passe temporaire
        emailService.sendSimpleMessage(savedUser.getEmail(), "Votre mot de passe temporaire",
                "Bonjour " + savedUser.getUsername() + ",\n\nVotre compte a été créé avec succès.\n" +
                        "Votre mot de passe temporaire est : " + generatedPassword + "\n\nVeuillez le modifier lors de votre première connexion.");

        return new AuthResponse(null, savedUser.getEmail(), "Inscription réussie, un email avec votre mot de passe temporaire vous a été envoyé.", savedUser.getRole().name());
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

    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangerMotDePasseRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé", HttpStatus.NOT_FOUND));


        boolean isPasswordCorrect = passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getPassword());

        if (!isPasswordCorrect) {
            throw new ApiException("Ancien mot de passe incorrect", HttpStatus.BAD_REQUEST);
        }

        utilisateur.setPassword(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateur.setForcePasswordChange(false);
        utilisateurRepository.save(utilisateur);

        // Envoyer un email de confirmation
        try {
            emailService.sendSimpleMessage(utilisateur.getEmail(), "Changement de mot de passe réussi",
                    "Bonjour " + utilisateur.getUsername() + ",\n\nVotre mot de passe a été modifié avec succès.\n" +
                            "Si vous n'êtes pas à l'origine de ce changement, veuillez contacter le support immédiatement.");
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de confirmation pour l'utilisateur : {}", utilisateur.getEmail());
            // Optionnel : Vous pourriez renvoyer une réponse d'erreur indiquant l'échec de l'email.
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getNouveauMotDePasse())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok("Mot de passe changé avec succès. Voici votre nouveau token : " + token);
    }
}
