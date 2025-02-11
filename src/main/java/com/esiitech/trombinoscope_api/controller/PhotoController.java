package com.esiitech.trombinoscope_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private static final String UPLOAD_DIR = "uploads/";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 Mo

    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deletePhoto(@PathVariable String filename) {
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        try {
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok("Photo supprimée avec succès");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide");
        }

        if (!ALLOWED_EXTENSIONS.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body("Format non autorisé. Seuls JPEG et PNG sont acceptés.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("Le fichier dépasse la taille maximale de 5 Mo.");
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());
            return ResponseEntity.ok("Fichier uploadé avec succès: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'upload");
        }
    }

    @PutMapping("/update/{filename}")
    public ResponseEntity<String> updatePhoto(@PathVariable String filename, @RequestParam("file") MultipartFile newFile) {
        Path oldFilePath = Paths.get(UPLOAD_DIR + filename);

        if (!Files.exists(oldFilePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Fichier non trouvé");
        }

        deletePhoto(filename);
        return uploadPhoto(newFile);
    }

    // Endpoint pour récupérer une photo
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            byte[] imageBytes = Files.readAllBytes(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", Files.probeContentType(filePath));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
