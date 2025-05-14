package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.ModifierUtilisateurRequest;
import com.esiitech.trombinoscope_api.DTOs.MotDePasseOublieRequest;
import com.esiitech.trombinoscope_api.DTOs.UtilisateurDto;
import com.esiitech.trombinoscope_api.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            content = @Content(schema = @Schema(implementation = UtilisateurDto.class)))
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UtilisateurDto>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @Operation(summary = "Obtenir un utilisateur par ID", description = "Récupère les détails d'un utilisateur spécifique.")
    @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès",
            content = @Content(schema = @Schema(implementation = UtilisateurDto.class)))
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDto> getUtilisateurById(@PathVariable Long id) {
        UtilisateurDto utilisateurDto = utilisateurService.getUtilisateurById(id);
        log.info("Utilisateur trouvé avec ID: {}", id);
        return ResponseEntity.ok(utilisateurDto);
    }

    @Operation(summary = "Modifier un utilisateur", description = "Modifie les informations d'un utilisateur existant.")
    @ApiResponse(responseCode = "200", description = "Utilisateur modifié avec succès",
            content = @Content(schema = @Schema(implementation = UtilisateurDto.class)))
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> modifierUtilisateur(@PathVariable Long id, @Valid @RequestBody ModifierUtilisateurRequest modifierUtilisateurRequest) {
        UtilisateurDto utilisateurModifie = utilisateurService.modifierUtilisateur(id, modifierUtilisateurRequest);
        log.info("Modification de l'utilisateur avec ID: {}", id);
        return ResponseEntity.ok(utilisateurModifie);
    }


    @Operation(summary = "Demander la réinitialisation du mot de passe", description = "Permet à l'utilisateur de demander une réinitialisation de son mot de passe en envoyant son email.")
    @ApiResponse(responseCode = "200", description = "Un email de réinitialisation a été envoyé.")
    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<String> motDePasseOublie(@Valid @RequestBody MotDePasseOublieRequest request) {
        String email = request.getEmail();
        log.info("Demande de réinitialisation du mot de passe pour l'email: {}", email);
        utilisateurService.demandeReinitialisationMotDePasse(email);
        return ResponseEntity.ok("Un email de réinitialisation a été envoyé.");
    }

    @Operation(summary = "Réinitialiser le mot de passe", description = "Permet de réinitialiser le mot de passe d'un utilisateur à l'aide d'un token valide.")
    @PostMapping("/reinitialiser-mot-de-passe")
    public ResponseEntity<String> reinitialiserMotDePasse(@RequestParam String token, @RequestParam String nouveauMotDePasse) {
        log.info("Tentative de réinitialisation du mot de passe avec le token: {}", token);
        utilisateurService.reinitialiserMotDePasse(token, nouveauMotDePasse);
        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }

    @Operation(summary = "Obtenir tous les utilisateurs actifs", description = "Récupère la liste des utilisateurs actifs")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/actifs")
    public ResponseEntity<List<UtilisateurDto>> getUtilisateursActifs() {
        return ResponseEntity.ok(utilisateurService.getUtilisateursActifs());
    }

    @Operation(summary = "Désactiver un utilisateur", description = "Désactive un utilisateur en fonction de son ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/desactiver/{id}")
    public ResponseEntity<String> desactiverUtilisateur(@PathVariable Long id) {
        utilisateurService.desactiverUtilisateur(id);
        log.info("Utilisateur désactivé avec succès, ID: {}", id);
        return ResponseEntity.ok("Utilisateur désactivé avec succès");
    }

    @Operation(summary = "Activer un utilisateur", description = "Active un utilisateur en fonction de son ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activer/{id}")
    public ResponseEntity<String> activerUtilisateur(@PathVariable Long id) {
        utilisateurService.activerUtilisateur(id);
        log.info("Utilisateur activé avec succès, ID: {}", id);
        return ResponseEntity.ok("Utilisateur activé avec succès");
    }

    @Operation(summary = "Changer le mot de passe après la connexion", description = "Permet à l'utilisateur de changer son mot de passe après s'être connecté.")
    @PostMapping("/changer-mot-de-passe")
    public ResponseEntity<String> changerMotDePasse(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestParam String nouveauMotDePasse) {
        utilisateurService.changerMotDePasse(userDetails.getUsername(), nouveauMotDePasse);
        return ResponseEntity.ok("Mot de passe changé avec succès.");
    }


    @Operation(
            summary = "Obtenir mon profil",
            description = "Récupère les informations du profil de l'utilisateur actuellement connecté."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Profil utilisateur récupéré avec succès.",
            content = @Content(schema = @Schema(implementation = UtilisateurDto.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Utilisateur non authentifié.",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mon-profil")
    public ResponseEntity<UtilisateurDto> getMonProfil() {
        return ResponseEntity.ok(utilisateurService.getMonProfil());
    }
    @Operation(
            summary = "Modifier mon profil",
            description = "Permet à l'utilisateur connecté de modifier son nom d'utilisateur et/ou son mot de passe."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Profil mis à jour avec succès.",
            content = @Content(schema = @Schema(implementation = UtilisateurDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Requête invalide (champs vides ou incorrects).",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Utilisateur non authentifié.",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/mon-profil")
    public ResponseEntity<UtilisateurDto> modifierMonProfil(
            @RequestParam(required = false) String nouveauUsername,
            @RequestParam(required = false) String nouveauMotDePasse
    ) {
        UtilisateurDto updatedUser = utilisateurService.modifierMonProfil(nouveauUsername, nouveauMotDePasse);
        return ResponseEntity.ok(updatedUser);
    }


    // Rechercher des utilisateurs par email ou username
    @GetMapping("/recherche")
    public ResponseEntity<List<UtilisateurDto>> rechercherUtilisateurs(
            @RequestParam("q") String recherche) {
        List<UtilisateurDto> resultats = utilisateurService.rechercherParUsernameOuEmail(recherche);
        return ResponseEntity.ok(resultats);
    }


    // Supprimer un utilisateur par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
