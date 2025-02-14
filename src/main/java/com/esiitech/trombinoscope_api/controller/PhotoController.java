package com.esiitech.trombinoscope_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@Tag(name = "Gestion des photos", description = "Endpoints pour gérer l'upload, la suppression et la récupération des photos")
public class PhotoController {

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);

    @Value("${upload.dir}")
    private String UPLOAD_DIR;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 Mo

    @Operation(summary = "Supprimer une photo", description = "Supprime une photo du serveur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Photo non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression de la photo")
    })
    @DeleteMapping("/{filename}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<String> deletePhoto(@PathVariable String filename) {
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return ResponseEntity.ok("Photo supprimée avec succès.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo non trouvée.");
            }
        } catch (IOException e) {
            logger.error("Erreur lors de la suppression du fichier", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression.");
        }
    }

    @Operation(summary = "Uploader une photo", description = "Ajoute une nouvelle photo au serveur.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploadée avec succès"),
            @ApiResponse(responseCode = "400", description = "Fichier non valide ou trop volumineux"),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de l'upload")
    })
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide.");
        }

        if (!ALLOWED_EXTENSIONS.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body("Format non autorisé. Seuls JPEG et PNG sont acceptés.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("Le fichier dépasse la taille maximale de 5 Mo.");
        }

        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(UPLOAD_DIR, uniqueFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("Fichier uploadé avec succès: " + uniqueFileName);
        } catch (IOException e) {
            logger.error("Erreur lors de l'upload de l'image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne lors de l'upload.");
        }
    }

    @Operation(summary = "Mettre à jour une photo", description = "Remplace une photo existante par une nouvelle.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour")
    })
    @PutMapping("/update/{filename}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<String> updatePhoto(@PathVariable String filename, @RequestParam("file") MultipartFile newFile) {
        Path oldFilePath = Paths.get(UPLOAD_DIR, filename);

        if (!Files.exists(oldFilePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fichier non trouvé.");
        }

        try {
            Files.deleteIfExists(oldFilePath);
            return uploadPhoto(newFile);
        } catch (IOException e) {
            logger.error("Erreur lors de la mise à jour de l'image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour.");
        }
    }

    @Operation(summary = "Récupérer une photo", description = "Récupère une photo du serveur par son nom de fichier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Fichier introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération de l'image")
    })
    @GetMapping("/{fileName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                logger.warn("Fichier introuvable ou illisible: " + fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = Files.readAllBytes(filePath);

            // Déterminer le type MIME de l'image
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Valeur par défaut
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Erreur lors de la récupération de l'image: " + fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
