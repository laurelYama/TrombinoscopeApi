package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.repository.EtudiantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantRepository etudiantRepository;

    public EtudiantController(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        Optional<Etudiant> etudiant = etudiantRepository.findById(id);
        return etudiant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public Etudiant createEtudiant(@RequestBody Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    @PutMapping("/{id}")
    public Etudiant updateEtudiant(@PathVariable Long id, @RequestBody Etudiant etudiantDetails) {
        return etudiantRepository.findById(id).map(etudiant -> {
            etudiant.setNom(etudiantDetails.getNom());
            etudiant.setPrenom(etudiantDetails.getPrenom());
            etudiant.setEmail(etudiantDetails.getEmail());
            etudiant.setGenre(etudiantDetails.getGenre());
            etudiant.setTelephone(etudiantDetails.getTelephone());
            etudiant.setNationalite(etudiantDetails.getNationalite());
            return etudiantRepository.save(etudiant);
        }).orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
    }

    @DeleteMapping("/{id}")
    public void deleteEtudiant(@PathVariable Long id) {
        etudiantRepository.deleteById(id);
    }

    @GetMapping("/filtrer")
    public List<Etudiant> filtrerEtudiants(
            @RequestParam(required = false) Long promotionId,
            @RequestParam(required = false) Long parcoursId,
            @RequestParam(required = false) Long specialiteId
    ) {
        if (promotionId != null && parcoursId != null && specialiteId != null) {
            return etudiantRepository.findByPromotionIdAndParcoursIdAndSpecialiteId(promotionId, parcoursId, specialiteId);
        } else if (promotionId != null) {
            return etudiantRepository.findByPromotionId(promotionId);
        } else if (parcoursId != null) {
            return etudiantRepository.findByParcoursId(parcoursId);
        } else if (specialiteId != null) {
            return etudiantRepository.findBySpecialiteId(specialiteId);
        } else {
            return etudiantRepository.findAll(); // Retourne tous les étudiants si aucun paramètre n'est fourni
        }
    }

    @PutMapping("/{id}/photo")
    public ResponseEntity<Etudiant> updatePhoto(@PathVariable Long id, @RequestParam("photoUrl") String photoUrl) {
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(id);

        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            etudiant.setPhoto(photoUrl);
            etudiantRepository.save(etudiant);
            return ResponseEntity.ok(etudiant);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

