package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Promotion;
import com.esiitech.trombinoscope_api.Exception.PromotionNotFoundException;
import com.esiitech.trombinoscope_api.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion avec ID " + id + " non trouvée"));
    }

    public Promotion createPromotion(Promotion promotion) {
        if (promotionRepository.existsByAnnee(promotion.getAnnee())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une promotion avec cette année existe déjà");
        }
        return promotionRepository.save(promotion);
    }

    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        return promotionRepository.findById(id)
                .map(promotion -> {
                    if (promotion.getAnnee() != promotionDetails.getAnnee()
                            && promotionRepository.existsByAnnee(promotionDetails.getAnnee())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une autre promotion avec cette année existe déjà !");
                    }
                    promotion.setAnnee(promotionDetails.getAnnee());
                    return promotionRepository.save(promotion);
                })
                .orElseThrow(() -> new PromotionNotFoundException("Promotion avec ID " + id + " non trouvée"));
    }

    public boolean existsByAnnee(int annee) {
        return promotionRepository.existsByAnnee(annee);
    }
}
