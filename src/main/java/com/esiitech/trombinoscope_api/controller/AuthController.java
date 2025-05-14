package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.AuthRequest;
import com.esiitech.trombinoscope_api.DTOs.AuthResponse;
import com.esiitech.trombinoscope_api.DTOs.ChangerMotDePasseRequest;
import com.esiitech.trombinoscope_api.DTOs.SetPasswordRequest;
import com.esiitech.trombinoscope_api.Entity.PasswordResetToken;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.ApiException;
import com.esiitech.trombinoscope_api.repository.PasswordResetTokenRepository;
import com.esiitech.trombinoscope_api.repository.UtilisateurRepository;
import com.esiitech.trombinoscope_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion des utilisateurs")
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un compte utilisateur et renvoie un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur", description = "Authentifie un utilisateur et renvoie un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "403", description = "Mot de passe à changer avant connexion")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/set-password")
    @Operation(summary = "Définir un mot de passe via un lien sécurisé", description = "Permet de définir un mot de passe à partir d’un lien reçu par email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe défini avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré")
    })
    public ResponseEntity<String> setPassword(@Valid @RequestBody SetPasswordRequest request) {
        // Vérifier si le mot de passe et la confirmation correspondent
        if (!request.getNouveauMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new ApiException("Le mot de passe et sa confirmation ne correspondent pas", HttpStatus.BAD_REQUEST);
        }

        // Vérifier si le token est valide
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ApiException("Token invalide ou expiré", HttpStatus.BAD_REQUEST));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ApiException("Le lien a expiré", HttpStatus.BAD_REQUEST);
        }

        Utilisateur utilisateur = resetToken.getUtilisateur();

        utilisateur.setPassword(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateur.setForcePasswordChange(false);
        utilisateur.setUpdatedAt(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        // Supprimer le token de réinitialisation après usage
        passwordResetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Mot de passe défini avec succès");
    }


}
