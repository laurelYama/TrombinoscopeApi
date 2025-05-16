package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.DTOs.EtudiantDto;
import com.esiitech.trombinoscope_api.Entity.Diplome;
import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.Exception.EtudiantNotFoundException;
import com.esiitech.trombinoscope_api.repository.EtudiantRepository;
import com.esiitech.trombinoscope_api.service.EtudiantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final EtudiantRepository etudiantRepository;
    private static final Logger logger = LoggerFactory.getLogger(EtudiantController.class);

    public EtudiantController(EtudiantService etudiantService, EtudiantRepository etudiantRepository) {
        this.etudiantService = etudiantService;
        this.etudiantRepository = etudiantRepository;
    }

    @Operation(summary = "Liste de tous les étudiants")
    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getAllEtudiants();
    }

    @Operation(summary = "Liste des étudiants actifs")
    @GetMapping("/actifs")
    public ResponseEntity<List<Etudiant>> getEtudiantsActifs() {
        return ResponseEntity.ok(etudiantService.getEtudiantsActifs());
    }

    @Operation(summary = "Détails d'un étudiant par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        return etudiantService.getEtudiantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Ajouter un étudiant")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NORMAL')")
    public ResponseEntity<Etudiant> ajouterEtudiant(@RequestBody Etudiant etudiant) {
        Etudiant nouveauEtudiant = etudiantService.ajouterEtudiant(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauEtudiant);
    }

    @Operation(summary = "Modifier un étudiant")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NORMAL')")
    public ResponseEntity<Etudiant> modifierEtudiant(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        try {
            return ResponseEntity.ok(etudiantService.modifierEtudiant(id, etudiant));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Activer un étudiant")
    @PutMapping("/activer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NORMAL')")
    public ResponseEntity<String> activerEtudiant(@PathVariable Long id) {
        try {
            etudiantService.activerEtudiant(id);
            return ResponseEntity.ok("Étudiant activé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @Operation(summary = "Désactiver un étudiant")
    @PutMapping("/desactiver/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NORMAL')")
    public ResponseEntity<String> desactiverEtudiant(@PathVariable Long id) {
        try {
            etudiantService.desactiverEtudiant(id);
            return ResponseEntity.ok("Étudiant désactivé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @Operation(summary = "Ajouter un diplôme à un étudiant")
    @PostMapping("/{etudiantId}/diplomes/{diplomeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NORMAL')")
    public ResponseEntity<Etudiant> ajouterDiplome(@PathVariable Long etudiantId, @PathVariable Long diplomeId) {
        Etudiant etudiant = etudiantService.ajouterDiplomeAEtudiant(etudiantId, diplomeId);
        return ResponseEntity.ok(etudiant);
    }

    @Operation(summary = "Liste des diplômes d’un étudiant")
    @PreAuthorize("hasAnyRole('ADMIN', 'NORMAL')")
    public ResponseEntity<List<Diplome>> getDiplomes(@PathVariable Long etudiantId) {
        return etudiantRepository.findById(etudiantId)
                .map(e -> ResponseEntity.ok(e.getDiplomes()))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Récupérer la photo d’un étudiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo récupérée avec succès", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "Étudiant ou photo non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getEtudiantPhoto(@PathVariable Long id) {
        try {
            Path photoPath = etudiantService.getPhotoPathByEtudiantId(id);
            if (!Files.exists(photoPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] imageBytes = Files.readAllBytes(photoPath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (EtudiantNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Uploader une photo")
    @PostMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<String> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            etudiantService.uploadPhoto(id, file);
            return ResponseEntity.ok("Photo ajoutée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout de la photo : " + e.getMessage());
        }
    }

    @Operation(summary = "Récupérer un étudiant avec ses promotions")
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getEtudiantWithPromo(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getEtudiantWithPromo(id));
    }

    @Operation(summary = "Lister tous les étudiants avec leurs promotions")
    @GetMapping("/with-promos")
    public List<EtudiantDto> getAllEtudiantsWithPromos() {
        return etudiantService.getAllEtudiantsWithPromos();
    }
}
