package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Promotion;
import com.esiitech.trombinoscope_api.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/promotions")
@CrossOrigin("*") // Permet les requêtes depuis le frontend
public class PromotionController {

    @Autowired
    private PromotionRepository promotionRepository;

    // Récupérer toutes les promotions
    @GetMapping
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    // Récupérer une promotion par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Long id) {
        Optional<Promotion> promotion = promotionRepository.findById(id);
        return promotion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Ajouter une nouvelle promotion
    @PostMapping
    public Promotion createPromotion(@RequestBody Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    // Modifier une promotion existante
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id, @RequestBody Promotion newPromotion) {
        return promotionRepository.findById(id)
                .map(promotion -> {
                    promotion.setAnnee(newPromotion.getAnnee());
                    return ResponseEntity.ok(promotionRepository.save(promotion));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Supprimer une promotion
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

