package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.EtudiantPromo;
import com.esiitech.trombinoscope_api.service.EtudiantPromoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/etudiant-promo")
@RequiredArgsConstructor
@Tag(name = "EtudiantPromo", description = "Gestion des promotions des étudiants")
public class EtudiantPromoController {

    private final EtudiantPromoService etudiantPromoService;

    @Operation(summary = "Ajouter une promotion à un étudiant", description = "Permet d'associer un étudiant à une promotion, un cycle et une spécialité.")
    @PostMapping("/ajouter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<EtudiantPromo> ajouterEtudiantPromo(@RequestBody EtudiantPromo etudiantPromo) {
        return ResponseEntity.ok(etudiantPromoService.ajouterEtudiantPromo(etudiantPromo));
    }

    @Operation(summary = "Filtrer les étudiants par promotion, cycle ou spécialité",
            description = "Filtre les étudiants en fonction des critères de promotion, cycle et spécialité.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des étudiants filtrée renvoyée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @GetMapping("/filtrer")
    public ResponseEntity<List<EtudiantPromo>> filtrerEtudiantsParPromo(
            @RequestParam(required = false) Long promotionId,
            @RequestParam(required = false) Long cycleId,
            @RequestParam(required = false) Long specialiteId) {

        List<EtudiantPromo> etudiants = etudiantPromoService.filtrerEtudiantsParPromo(promotionId, cycleId, specialiteId);
        return ResponseEntity.ok(etudiants);
    }


    @Operation(summary = "Modifier un étudiant dans une promotion",
            description = "Met à jour les informations d'un étudiant dans une promotion spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etudiant modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Etudiant non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<EtudiantPromo> modifierEtudiantPromo(@PathVariable Long id, @RequestBody EtudiantPromo updatedPromo) {
        try {
            EtudiantPromo etudiantPromoModifie = etudiantPromoService.modifierEtudiantPromo(id, updatedPromo);
            return ResponseEntity.ok(etudiantPromoModifie);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(summary = "Récupérer les promotions d'un étudiant", description = "Retourne la liste des promotions, cycles et spécialités associés à un étudiant.")
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<EtudiantPromo>> getPromotionsByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(etudiantPromoService.getPromotionsByEtudiant(etudiantId));
    }

    @Operation(summary = "Lister toutes les inscriptions d'étudiants", description = "Retourne la liste complète des étudiants associés aux promotions, cycles et spécialités.")
    @GetMapping("/liste")
    public ResponseEntity<List<EtudiantPromo>> getEtudiantPromos() {
        return ResponseEntity.ok(etudiantPromoService.getEtudiantPromos());
    }

    @Operation(summary = "Récupérer les étudiants par promotion", description = "Retourne la liste des étudiants inscrits dans une promotion spécifique.")
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<EtudiantPromo>> getEtudiantByPromotion(@PathVariable Long promotionId) {
        return ResponseEntity.ok(etudiantPromoService.getEtudiantByPromotion(promotionId));
    }

    @Operation(summary = "Récupérer les étudiants par cycle", description = "Retourne la liste des étudiants appartenant à un cycle donné.")
    @GetMapping("/cycle/{cycleId}")
    public ResponseEntity<List<EtudiantPromo>> getEtudiantByCycle(@PathVariable Long cycleId) {
        return ResponseEntity.ok(etudiantPromoService.getEtudiantByCycle(cycleId));
    }

    @Operation(summary = "Récupérer les étudiants par spécialité", description = "Retourne la liste des étudiants suivant une spécialité spécifique.")
    @GetMapping("/specialite/{specialiteId}")
    public ResponseEntity<List<EtudiantPromo>> getEtudiantBySpecialite(@PathVariable Long specialiteId) {
        return ResponseEntity.ok(etudiantPromoService.getEtudiantBySpecialite(specialiteId));
    }
}
