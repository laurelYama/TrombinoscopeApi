package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.DiplomeRequest;
import com.esiitech.trombinoscope_api.Entity.Diplome;
import com.esiitech.trombinoscope_api.service.DiplomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/diplomes")
@Tag(name = "Diplômes", description = "Gestion des diplômes des étudiants")
public class DiplomeController {

    @Autowired
    private DiplomeService diplomeService;

    // 🔹 Créer un diplôme
    @Operation(summary = "Créer un diplôme", description = "Ajoute un nouveau diplôme en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDiplome(@RequestBody DiplomeRequest request) {
        try {
            Diplome diplome = diplomeService.createDiplome(request);
            return ResponseEntity.ok(diplome);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔹 Obtenir un diplôme par son numéro
    @Operation(summary = "Obtenir un diplôme", description = "Récupère un diplôme en fonction de son numéro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme trouvé"),
            @ApiResponse(responseCode = "404", description = "Diplôme non trouvé")
    })
    @GetMapping("/{numero}")
    public ResponseEntity<?> getDiplomeByNumero(@PathVariable String numero) {
        try {
            return ResponseEntity.ok(diplomeService.getDiplomeByNumero(numero));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Mettre à jour un diplôme
    @Operation(summary = "Mettre à jour un diplôme", description = "Modifie les informations d’un diplôme existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Diplôme non trouvé")
    })
    @PutMapping("/{numero}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDiplome(@PathVariable String numero, @RequestBody DiplomeRequest request) {
        try {
            Diplome diplome = diplomeService.updateDiplome(numero, request);
            return ResponseEntity.ok(diplome);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Supprimer un diplôme
    @Operation(summary = "Supprimer un diplôme", description = "Supprime un diplôme en fonction de son numéro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Diplôme non trouvé")
    })
    @DeleteMapping("/{numero}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDiplome(@PathVariable String numero) {
        try {
            diplomeService.deleteDiplome(numero);
            return ResponseEntity.ok("Diplôme supprimé avec succès.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
