package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.service.EtudiantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@Tag(name = "Etudiants", description = "Gestion des étudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private static final String UPLOAD_DIR = "uploads";
    private static final Logger logger = LoggerFactory.getLogger(EtudiantController.class);

    @Autowired
    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @Operation(summary = "Récupérer tous les étudiants", description = "Renvoie la liste complète des étudiants.")
    @ApiResponse(responseCode = "200", description = "Liste des étudiants récupérée avec succès")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getAllEtudiants();
    }

    @Operation(summary = "Récupérer un étudiant par ID", description = "Renvoie un étudiant spécifique basé sur son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé",
                    content = @Content(schema = @Schema(implementation = Etudiant.class))),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Etudiant> getEtudiantById(@Parameter(description = "ID de l'étudiant") @PathVariable Long id) {
        return etudiantService.getEtudiantById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un nouvel étudiant", description = "Ajoute un étudiant à la base de données.")
    @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Etudiant> createEtudiant(@RequestBody Etudiant etudiant) {
        Etudiant savedEtudiant = etudiantService.saveEtudiant(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEtudiant);
    }

    @Operation(summary = "Mettre à jour un étudiant", description = "Modifie les informations d'un étudiant existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Étudiant mis à jour"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Etudiant> updateEtudiant(@Parameter(description = "ID de l'étudiant") @PathVariable Long id,
                                                   @RequestBody Etudiant updatedEtudiant) {
        return etudiantService.getEtudiantById(id)
                .map(etudiant -> {
                    updatedEtudiant.setId(id);
                    Etudiant savedEtudiant = etudiantService.saveEtudiant(updatedEtudiant);
                    return ResponseEntity.ok(savedEtudiant);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un étudiant", description = "Supprime un étudiant basé sur son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Étudiant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<Void> deleteEtudiant(@Parameter(description = "ID de l'étudiant") @PathVariable Long id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des étudiants", description = "Recherche des étudiants par mot-clé.")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public Page<Etudiant> searchEtudiants(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return etudiantService.searchEtudiants(keyword, page, size);
    }

    @Operation(summary = "Filtrer les étudiants", description = "Filtre les étudiants en fonction de la promotion, du parcours et de la spécialité.")
    @GetMapping("/filtrer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public List<Etudiant> filtrerEtudiants(@RequestParam(required = false) Long promotionId,
                                           @RequestParam(required = false) Long parcoursId,
                                           @RequestParam(required = false) Long specialiteId) {
        return etudiantService.filtrerEtudiants(promotionId, parcoursId, specialiteId);
    }

    @Operation(summary = "Uploader une photo", description = "Ajoute une photo à un étudiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo ajoutée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'ajout de la photo")
    })
    @PostMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<String> uploadPhoto(@Parameter(description = "ID de l'étudiant") @PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) {
        try {
            etudiantService.uploadPhoto(id, file);
            return ResponseEntity.ok("Photo ajoutée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout de la photo : " + e.getMessage());
        }
    }

    @Operation(summary = "Récupérer une photo", description = "Renvoie la photo d'un étudiant en fonction du nom de fichier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo trouvée"),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée")
    })
    @GetMapping("/photo/{fileName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<byte[]> getPhoto(@Parameter(description = "Nom du fichier de la photo") @PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(filePath));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Erreur lors de la récupération de l'image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
