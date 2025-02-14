package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.repository.EtudiantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public Optional<Etudiant> getEtudiantById(Long id) {
        return etudiantRepository.findById(id);
    }

    public Etudiant saveEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public void deleteEtudiant(Long id) {
        if (!etudiantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant non trouvé");
        }
        etudiantRepository.deleteById(id);
    }

    public Page<Etudiant> searchEtudiants(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return etudiantRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword, pageable);
    }

    public Path getPhotoPathByEtudiantId(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Étudiant non trouvé avec l'ID : " + id));

        String photoPath = etudiant.getPhotoPath(); // Assure-toi que la classe `Etudiant` a bien un champ `photoPath`
        if (photoPath == null || photoPath.isEmpty()) {
            throw new NoSuchElementException("Aucune photo trouvée pour l'étudiant avec l'ID : " + id);
        }

        return Paths.get("uploads/photos").resolve(photoPath);
    }

    public List<Etudiant> filtrerEtudiants(Long promotionId, Long parcoursId, Long specialiteId) {
        List<Etudiant> etudiants;

        if (promotionId != null && parcoursId != null && specialiteId != null) {
            etudiants = etudiantRepository.findByPromotionIdAndParcoursIdAndSpecialiteId(promotionId, parcoursId, specialiteId);
        } else if (promotionId != null) {
            etudiants = etudiantRepository.findByPromotionId(promotionId);
        } else if (parcoursId != null) {
            etudiants = etudiantRepository.findByParcoursId(parcoursId);
        } else if (specialiteId != null) {
            etudiants = etudiantRepository.findBySpecialiteId(specialiteId);
        } else {
            etudiants = etudiantRepository.findAll();
        }

        if (etudiants.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun étudiant trouvé avec ces critères");
        }

        return etudiants;
    }

    public void uploadPhoto(Long id, MultipartFile file) throws Exception {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Étudiant non trouvé !"));

        // Définir le répertoire où enregistrer l'image
        String uploadDir = "uploads";
        Files.createDirectories(Path.of(uploadDir)); // Crée le dossier s'il n'existe pas

        // Sauvegarde du fichier
        String filename = id + "_" + file.getOriginalFilename();
        Path filePath = Path.of(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Mise à jour de l'étudiant avec le chemin de l'image
        etudiant.setPhotoPath(filename);
        etudiantRepository.save(etudiant);
    }
}
