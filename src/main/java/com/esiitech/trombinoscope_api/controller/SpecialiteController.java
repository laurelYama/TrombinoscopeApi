package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Specialite;
import com.esiitech.trombinoscope_api.service.SpecialiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialites")
@CrossOrigin("*") // Autorise l'accès depuis le frontend
@Tag(name = "Spécialités", description = "Gestion des spécialités")
public class SpecialiteController {

    private final SpecialiteService specialiteService;

    public SpecialiteController(SpecialiteService specialiteService) {
        this.specialiteService = specialiteService;
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les spécialités", description = "Renvoie la liste de toutes les spécialités disponibles.")
    public List<Specialite> getAllSpecialites() {
        return specialiteService.getAllSpecialites();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une spécialité par ID", description = "Renvoie une spécialité spécifique en fonction de son identifiant.")
    public ResponseEntity<Specialite> getSpecialiteById(@PathVariable Long id) {
        try {
            Specialite specialite = specialiteService.getSpecialiteById(id);
            return ResponseEntity.ok(specialite);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Ajouter une nouvelle spécialité", description = "Ajoute une nouvelle spécialité et la renvoie.")
    public ResponseEntity<Specialite> createSpecialite(@Valid @RequestBody Specialite specialite) {
        Specialite savedSpecialite = specialiteService.createSpecialite(specialite);
        return ResponseEntity.ok(savedSpecialite);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Modifier une spécialité", description = "Met à jour une spécialité existante avec les nouvelles informations fournies.")
    public ResponseEntity<Specialite> updateSpecialite(@PathVariable Long id, @RequestBody Specialite newSpecialite) {
        try {
            return ResponseEntity.ok(specialiteService.updateSpecialite(id, newSpecialite));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
