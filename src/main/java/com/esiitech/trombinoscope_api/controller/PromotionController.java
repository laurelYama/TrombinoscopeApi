package com.esiitech.trombinoscope_api.controller;

import com.esiitech.trombinoscope_api.Entity.Promotion;
import com.esiitech.trombinoscope_api.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@Tag(name = "Promotions", description = "API pour gérer les promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @Operation(summary = "Récupérer toutes les promotions",
            description = "Permet d'obtenir la liste de toutes les promotions.")
    @GetMapping
    public List<Promotion> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    @Operation(summary = "Récupérer une promotion par ID",
            description = "Permet d'obtenir une promotion spécifique en fournissant son ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Promotion trouvée",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Promotion.class))),
                    @ApiResponse(responseCode = "404", description = "Promotion non trouvée")
            })
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Long id) {
        try {
            Promotion promotion = promotionService.getPromotionById(id);
            return ResponseEntity.ok(promotion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Ajouter une nouvelle promotion",
            description = "Permet de créer une nouvelle promotion.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        if (promotionService.existsByAnnee(promotion.getAnnee())) {
            return ResponseEntity.badRequest().build(); // Empêche les doublons
        }
        Promotion savedPromotion = promotionService.createPromotion(promotion);
        return ResponseEntity.ok(savedPromotion);
    }

    @Operation(summary = "Modifier une promotion existante",
            description = "Permet de mettre à jour une promotion existante avec un nouvel ID.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id, @RequestBody Promotion newPromotion) {
        try {
            return ResponseEntity.ok(promotionService.updatePromotion(id, newPromotion));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
