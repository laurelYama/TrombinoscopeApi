package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.AuthRequest;
import com.esiitech.trombinoscope_api.DTOs.ChangerMotDePasseRequest;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utilisateurs", description = "API pour la gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @Operation(summary = "Créer un utilisateur", description = "Ajoute un nouvel utilisateur à la base de données. Accessible uniquement aux administrateurs.")
    @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès",
            content = @Content(schema = @Schema(implementation = Utilisateur.class)))
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    @PostMapping("/ajouter")
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody AuthRequest utilisateurRequest) {
        log.info("Création d'un nouvel utilisateur: {}", utilisateurRequest.getEmail());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(utilisateurRequest.getEmail());
        utilisateur.setPassword(utilisateurRequest.getPassword());

        Utilisateur newUser = utilisateurService.creerUtilisateur(utilisateur);
        return ResponseEntity.ok(newUser);
    }

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

    @Operation(summary = "Changer le mot de passe", description = "Permet à un utilisateur de changer son mot de passe.")
    @ApiResponse(responseCode = "200", description = "Mot de passe modifié avec succès",
            content = @Content(schema = @Schema(implementation = Utilisateur.class)))
    @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    @PutMapping("/{id}/changer-mot-de-passe")
    public ResponseEntity<Utilisateur> changerMotDePasse(@PathVariable Long id, @Valid @RequestBody ChangerMotDePasseRequest request) {
        Utilisateur utilisateur = utilisateurService.changerMotDePasse(id, request.getAncienMotDePasse(), request.getNouveauMotDePasse());
        log.info("Mot de passe changé pour l'utilisateur avec ID: {}", id);
        return ResponseEntity.ok(utilisateur);
    }
}
