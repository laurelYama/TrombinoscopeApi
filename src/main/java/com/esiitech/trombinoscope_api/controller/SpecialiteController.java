package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Specialite;
import com.esiitech.trombinoscope_api.repository.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/specialites")
@CrossOrigin("*") // Autorise l'accès depuis le frontend
public class SpecialiteController {

    @Autowired
    private SpecialiteRepository specialiteRepository;

    // Récupérer toutes les spécialités
    @GetMapping
    public List<Specialite> getAllSpecialites() {
        return specialiteRepository.findAll();
    }

    // Récupérer une spécialité par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Specialite> getSpecialiteById(@PathVariable Long id) {
        Optional<Specialite> specialite = specialiteRepository.findById(id);
        return specialite.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter une nouvelle spécialité
    @PostMapping
    public Specialite createSpecialite(@RequestBody Specialite specialite) {
        return specialiteRepository.save(specialite);
    }

    // Modifier une spécialité existante
    @PutMapping("/{id}")
    public ResponseEntity<Specialite> updateSpecialite(@PathVariable Long id, @RequestBody Specialite newSpecialite) {
        return specialiteRepository.findById(id)
                .map(specialite -> {
                    specialite.setNom(newSpecialite.getNom());
                    return ResponseEntity.ok(specialiteRepository.save(specialite));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer une spécialité
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialite(@PathVariable Long id) {
        if (specialiteRepository.existsById(id)) {
            specialiteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
