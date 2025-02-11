package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Parcours;
import com.esiitech.trombinoscope_api.repository.ParcoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/parcours")
@CrossOrigin("*") // Autorise l'accès depuis le frontend
public class ParcoursController {

    @Autowired
    private ParcoursRepository parcoursRepository;

    // Récupérer tous les parcours
    @GetMapping
    public List<Parcours> getAllParcours() {
        return parcoursRepository.findAll();
    }

    // Récupérer un parcours par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Parcours> getParcoursById(@PathVariable Long id) {
        Optional<Parcours> parcours = parcoursRepository.findById(id);
        return parcours.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter un nouveau parcours
    @PostMapping
    public Parcours createParcours(@RequestBody Parcours parcours) {
        return parcoursRepository.save(parcours);
    }

    // Modifier un parcours existant
    @PutMapping("/{id}")
    public ResponseEntity<Parcours> updateParcours(@PathVariable Long id, @RequestBody Parcours newParcours) {
        return parcoursRepository.findById(id)
                .map(parcours -> {
                    parcours.setNiveau(newParcours.getNiveau());
                    return ResponseEntity.ok(parcoursRepository.save(parcours));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer un parcours
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcours(@PathVariable Long id) {
        if (parcoursRepository.existsById(id)) {
            parcoursRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

