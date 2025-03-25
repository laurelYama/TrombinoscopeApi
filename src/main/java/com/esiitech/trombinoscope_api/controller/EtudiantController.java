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
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/etudiants")
@CrossOrigin("*") // Permettre les requêtes depuis le front-end
public class EtudiantController {

    private final EtudiantService etudiantService;
    private static final String UPLOAD_DIR = "uploads";
    private static final Logger logger = LoggerFactory.getLogger(EtudiantController.class);
    private final EtudiantRepository etudiantRepository;

    public EtudiantController(EtudiantService etudiantService, EtudiantRepository etudiantRepository) {
        this.etudiantService = etudiantService;
        this.etudiantRepository = etudiantRepository;
    }


    @Operation(summary = "Récupérer tous les étudiants", description = "Retourne la liste complète des étudiants enregistrés.")
    @ApiResponse(responseCode = "200", description = "Liste des étudiants récupérée avec succès.")
    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getAllEtudiants();
    }

    @Operation(summary = "Récupérer tous les étudiants actifs", description = "Retourne la liste des étudiants ayant le statut actif.")
    @ApiResponse(responseCode = "200", description = "Liste des étudiants actifs récupérée avec succès.")
    @GetMapping("/actifs")
    public ResponseEntity<List<Etudiant>> getEtudiantsActifs() {
        List<Etudiant> etudiantsActifs = etudiantService.getEtudiantsActifs();
        return ResponseEntity.ok(etudiantsActifs);
    }

    @Operation(summary = "Récupérer un étudiant par ID", description = "Retourne un étudiant spécifique en fonction de son ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        Optional<Etudiant> etudiant = etudiantService.getEtudiantById(id);
        return etudiant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{etudiantId}/diplomes/{diplomeId}")
    public ResponseEntity<Etudiant> ajouterDiplome(@PathVariable Long etudiantId, @PathVariable Long diplomeId) {
        Etudiant etudiant = etudiantService.ajouterDiplomeAEtudiant(etudiantId, diplomeId);
        return ResponseEntity.ok(etudiant);
    }

    @GetMapping("/{etudiantId}/diplomes")
    public ResponseEntity<List<Diplome>> getDiplomes(@PathVariable Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        return ResponseEntity.ok(etudiant.getDiplomes());
    }


    @Operation(
            summary = "Récupérer la photo d'un étudiant",
            description = "Retourne l'image associée à un étudiant donné par son ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image trouvée et retournée avec succès",
                    content = @Content(mediaType = "image/jpeg",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé ou photo absente"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getEtudiantPhoto(@PathVariable Long id) {
        try {
            Path photoPath = etudiantService.getPhotoPathByEtudiantId(id);

            if (!Files.exists(photoPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = Files.readAllBytes(photoPath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Par défaut, on suppose que c'est une image JPEG

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (EtudiantNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Ajouter un étudiant", description = "Ajoute un nouvel étudiant à la base de données.")
    @ApiResponse(responseCode = "201", description = "Étudiant ajouté avec succès")
    @PostMapping
    public ResponseEntity<Etudiant> ajouterEtudiant(@RequestBody Etudiant etudiant) {
        Etudiant nouveauEtudiant = etudiantService.ajouterEtudiant(etudiant);
        return ResponseEntity.status(201).body(nouveauEtudiant);
    }

    @Operation(summary = "Modifier un étudiant", description = "Modifie les informations d'un étudiant existant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> modifierEtudiant(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        try {
            Etudiant etudiantModifie = etudiantService.modifierEtudiant(id, etudiant);
            return ResponseEntity.ok(etudiantModifie);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Activer un étudiant", description = "Active le statut d'un étudiant en mettant son état à 'actif'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant activé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'activation")
    })
    @PutMapping("/activer/{id}")
    public ResponseEntity<String> activerEtudiant(@PathVariable Long id) {
        try {
            etudiantService.activerEtudiant(id);
            return ResponseEntity.ok("Étudiant activé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @Operation(
            summary = "Récupérer un étudiant avec ses promotions",
            description = "Retourne les informations d'un étudiant ainsi que ses promotions associées.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès"),
                    @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
            }
    )
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getEtudiantWithPromo(
            @Parameter(description = "ID de l'étudiant", required = true) @PathVariable Long id
    ) {
        return ResponseEntity.ok(etudiantService.getEtudiantWithPromo(id));
    }

    @Operation(
            summary = "Lister tous les étudiants avec leurs promotions",
            description = "Retourne une liste de tous les étudiants avec leurs promotions associées.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Succès")
            }
    )
    @GetMapping("/with-promos")
    public List<EtudiantDto> getAllEtudiantsWithPromos() {
        return etudiantService.getAllEtudiantsWithPromos();
    }

    @Operation(summary = "Désactiver un étudiant", description = "Désactive un étudiant en mettant son état à 'inactif'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant désactivé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la désactivation")
    })
    @PutMapping("/desactiver/{id}")
    public ResponseEntity<String> desactiverEtudiant(@PathVariable Long id) {
        try {
            etudiantService.desactiverEtudiant(id);
            return ResponseEntity.ok("Étudiant désactivé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
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
