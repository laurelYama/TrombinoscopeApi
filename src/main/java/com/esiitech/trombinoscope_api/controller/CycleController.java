package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Cycle;
import com.esiitech.trombinoscope_api.service.CycleService;
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
@RequestMapping("/api/cycles")
@CrossOrigin("*")
@Tag(name = "Cycles", description = "Gestion des cycles étudiants")
public class CycleController {

    @Autowired
    private CycleService cycleService;

    @Operation(summary = "Récupérer tous tout les cycles", description = "Renvoie la liste des cycles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des cycles récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cycle> getAllParcours() {
        return cycleService.getAllCycle();
    }

    @Operation(summary = "Récupérer un cycles par ID", description = "Renvoie un cycle spécifique en fonction de son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cycle trouvé"),
            @ApiResponse(responseCode = "404", description = "Cycle non trouvé")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cycle> getParcoursById(@PathVariable Long id) {
        return ResponseEntity.ok(cycleService.getCycleById(id));
    }

    @Operation(summary = "Créer un nouveau cycle", description = "Ajoute un nouveau cycle à la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cycle créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cycle createParcours(@RequestBody Cycle cycle) {
        return cycleService.createCycle(cycle);
    }

    @Operation(summary = "Mettre à jour un cycle", description = "Met à jour les informations d'un cycle existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cycle mis à jour"),
            @ApiResponse(responseCode = "404", description = "Cycle non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cycle> updateParcours(@PathVariable Long id, @RequestBody Cycle newCycle) {
        return ResponseEntity.ok(cycleService.updateCycle(id, newCycle));
    }
}
