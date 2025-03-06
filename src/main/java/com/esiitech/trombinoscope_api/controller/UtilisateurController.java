package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.MotDePasseOublieRequest;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import com.esiitech.trombinoscope_api.Exception.UtilisateurNotFoundException;
import com.esiitech.trombinoscope_api.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utilisateurs", description = "API pour la gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @Operation(summary = "Lister tous les utilisateurs", description = "Retourne la liste de tous les utilisateurs. Accessible uniquement aux administrateurs.")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès",
            content = @Content(schema = @Schema(implementation = Utilisateur.class)))
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @Operation(summary = "Obtenir un utilisateur par ID", description = "Récupère les détails d'un utilisateur spécifique. Accessible uniquement aux administrateurs.")
    @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès",
            content = @Content(schema = @Schema(implementation = Utilisateur.class)))
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
        log.info("Utilisateur trouvé avec ID: {}", id);
        return ResponseEntity.ok(utilisateur);
    }

    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur de la base de données. Accessible uniquement aux administrateurs.")
    @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
        log.info("Utilisateur supprimé avec ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Modifier un utilisateur", description = "Modifie les informations d'un utilisateur existant. Accessible uniquement aux administrateurs.")
    @ApiResponse(responseCode = "200", description = "Utilisateur modifié avec succès",
            content = @Content(schema = @Schema(implementation = Utilisateur.class)))
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> modifierUtilisateur(@PathVariable Long id, @Valid @RequestBody Utilisateur updatedUser) {
        Utilisateur utilisateur = utilisateurService.modifierUtilisateur(id, updatedUser);
        log.info("Modification de l'utilisateur avec ID: {}", id);
        return ResponseEntity.ok(utilisateur);
    }

    @Operation(summary = "Demander la réinitialisation du mot de passe", description = "Permet à l'utilisateur de demander une réinitialisation de son mot de passe en envoyant son email.")
    @ApiResponse(responseCode = "200", description = "Un email de réinitialisation a été envoyé.")
    @ApiResponse(responseCode = "400", description = "Email requis")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<String> motDePasseOublie(@RequestBody MotDePasseOublieRequest request) {
        String email = request.getEmail();

        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'email est requis.");
        }

        try {
            utilisateurService.demandeReinitialisationMotDePasse(email);
            return ResponseEntity.ok("Un email de réinitialisation a été envoyé.");
        } catch (UtilisateurNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé.");
        }
    }

    @Operation(summary = "Réinitialiser le mot de passe", description = "Permet de réinitialiser le mot de passe d'un utilisateur à l'aide d'un token valide.")
    @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès.")
    @ApiResponse(responseCode = "400", description = "Token invalide ou expiré")
    @PostMapping("/reinitialiser-mot-de-passe")
    public ResponseEntity<String> reinitialiserMotDePasse(@RequestParam String token, @RequestParam String nouveauMotDePasse) {
        try {
            utilisateurService.reinitialiserMotDePasse(token, nouveauMotDePasse);
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
