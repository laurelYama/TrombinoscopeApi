package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.DTOs.EtudiantDto;
import com.esiitech.trombinoscope_api.Entity.Diplome;
import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.Exception.EtudiantNotFoundException;
import com.esiitech.trombinoscope_api.repository.DiplomeRepository;
import com.esiitech.trombinoscope_api.repository.EtudiantPromoRepository;
import com.esiitech.trombinoscope_api.repository.EtudiantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final EtudiantPromoRepository etudiantPromoRepository;
    private final DiplomeRepository diplomeRepository; // Ajout du DiplomeRepository

    @Value("${app.upload.dir}")
    private String uploadDir;

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public Optional<Etudiant> getEtudiantById(Long id) {
        return etudiantRepository.findById(id);
    }

    public List<EtudiantDto> getAllEtudiantsWithPromos() {
        return etudiantRepository.findAll().stream()
                .map(etudiant -> new EtudiantDto(etudiant, etudiantPromoRepository.findByEtudiantId(etudiant.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Etudiant ajouterEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    public EtudiantDto getEtudiantWithPromo(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new EtudiantNotFoundException("Étudiant non trouvé"));
        return new EtudiantDto(etudiant, etudiantPromoRepository.findByEtudiantId(id));
    }

    @Transactional
    public Etudiant modifierEtudiant(Long id, Etudiant etudiantModifie) {
        return etudiantRepository.findById(id).map(etudiant -> {
            etudiant.setNom(etudiantModifie.getNom());
            etudiant.setPrenom(etudiantModifie.getPrenom());
            etudiant.setEmail(etudiantModifie.getEmail());
            etudiant.setActif(etudiantModifie.isActif());
            return etudiantRepository.save(etudiant);
        }).orElseThrow(() -> new EtudiantNotFoundException("Étudiant non trouvé avec l'ID: " + id));
    }

    @Transactional
    public Etudiant ajouterDiplomeAEtudiant(Long etudiantId, Long diplomeId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Diplome diplome = diplomeRepository.findById(diplomeId)
                .orElseThrow(() -> new RuntimeException("Diplôme non trouvé"));

        etudiant.getDiplomes().add(diplome);
        return etudiantRepository.save(etudiant);
    }

    @Transactional
    public void activerEtudiant(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new EtudiantNotFoundException("Étudiant non trouvé"));
        etudiant.setActif(true);
        etudiantRepository.save(etudiant);
    }

    public List<Etudiant> getEtudiantsActifs() {
        return etudiantRepository.findByActifTrue();
    }

    @Transactional
    public void desactiverEtudiant(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new EtudiantNotFoundException("Étudiant non trouvé"));
        etudiant.setActif(false);
        etudiantRepository.save(etudiant);
    }

    public Path getPhotoPathByEtudiantId(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new EtudiantNotFoundException("Étudiant non trouvé avec l'ID : " + id));

        if (etudiant.getPhotoPath() == null || etudiant.getPhotoPath().isEmpty()) {
            throw new NoSuchElementException("Aucune photo trouvée pour l'étudiant avec l'ID : " + id);
        }
        return Paths.get(uploadDir).resolve(etudiant.getPhotoPath());
    }

    @Transactional
    public void uploadPhoto(Long id, MultipartFile file) throws IOException {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new EtudiantNotFoundException("Étudiant non trouvé !"));

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Seuls les fichiers images sont autorisés !");
        }

        final long MAX_SIZE = 2 * 1024 * 1024;
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("La taille de l'image ne doit pas dépasser 2 Mo !");
        }

        Files.createDirectories(Paths.get(uploadDir));
        String filename = id + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        etudiant.setPhotoPath(filename);
        etudiantRepository.save(etudiant);
    }
}
