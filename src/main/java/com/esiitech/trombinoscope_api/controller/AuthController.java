package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.AuthRequest;
import com.esiitech.trombinoscope_api.DTOs.AuthResponse;
import com.esiitech.trombinoscope_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints pour l'inscription et la connexion des utilisateurs")
public class AuthController {

    private final AuthService authService;
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
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }
}
