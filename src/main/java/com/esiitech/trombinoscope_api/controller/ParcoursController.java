package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Parcours;
import com.esiitech.trombinoscope_api.service.ParcoursService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcours")
@CrossOrigin("*")
@Tag(name = "Parcours", description = "Gestion des parcours étudiants")
public class ParcoursController {

    @Autowired
    private ParcoursService parcoursService;

    @Operation(summary = "Récupérer tous les parcours", description = "Renvoie la liste de tous les parcours.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des parcours récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public List<Parcours> getAllParcours() {
        return parcoursService.getAllParcours();
    }

    @Operation(summary = "Récupérer un parcours par ID", description = "Renvoie un parcours spécifique en fonction de son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcours trouvé"),
            @ApiResponse(responseCode = "404", description = "Parcours non trouvé")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Parcours> getParcoursById(@PathVariable Long id) {
        return ResponseEntity.ok(parcoursService.getParcoursById(id));
    }

    @Operation(summary = "Créer un nouveau parcours", description = "Ajoute un nouveau parcours à la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parcours créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public Parcours createParcours(@RequestBody Parcours parcours) {
        return parcoursService.createParcours(parcours);
    }

    @Operation(summary = "Mettre à jour un parcours", description = "Met à jour les informations d'un parcours existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcours mis à jour"),
            @ApiResponse(responseCode = "404", description = "Parcours non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Parcours> updateParcours(@PathVariable Long id, @RequestBody Parcours newParcours) {
        return ResponseEntity.ok(parcoursService.updateParcours(id, newParcours));
    }

    @Operation(summary = "Supprimer un parcours", description = "Supprime un parcours de la base de données en fonction de son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Parcours supprimé"),
            @ApiResponse(responseCode = "404", description = "Parcours non trouvé")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Void> deleteParcours(@PathVariable Long id) {
        parcoursService.deleteParcours(id);
        return ResponseEntity.noContent().build();
    }
}
