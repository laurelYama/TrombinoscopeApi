package com.esiitech.trombinoscope_api.service;


import com.esiitech.trombinoscope_api.Entity.Promotion;
import com.esiitech.trombinoscope_api.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findById(id);
    }

    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        return promotionRepository.findById(id).map(promotion -> {
            promotion.setAnnee(promotionDetails.getAnnee());
            return promotionRepository.save(promotion);
        }).orElseThrow(() -> new RuntimeException("Promotion non trouvée"));
    }

    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
}

