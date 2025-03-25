package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.EtudiantPromo;
import com.esiitech.trombinoscope_api.repository.EtudiantPromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EtudiantPromoService {

    private final EtudiantPromoRepository etudiantPromoRepository;

    public EtudiantPromo ajouterEtudiantPromo(EtudiantPromo etudiantPromo) {
        return etudiantPromoRepository.save(etudiantPromo);
    }

    public List<EtudiantPromo> getPromotionsByEtudiant(Long etudiantId) {
        return etudiantPromoRepository.findByEtudiantId(etudiantId);
    }

    public List<EtudiantPromo> filtrerEtudiantsParPromo(Long promotionId, Long cycleId, Long specialiteId) {
        if (promotionId != null && cycleId != null && specialiteId != null) {
            return etudiantPromoRepository.findByPromotionIdAndCycleIdAndSpecialiteId(promotionId, cycleId, specialiteId);
        } else if (promotionId != null) {
            return etudiantPromoRepository.findByPromotionId(promotionId);
        } else if (cycleId != null) {
            return etudiantPromoRepository.findByCycleId(cycleId);
        } else if (specialiteId != null) {
            return etudiantPromoRepository.findBySpecialiteId(specialiteId);
        } else {
            return etudiantPromoRepository.findAll();
        }
    }

    public List<EtudiantPromo> getEtudiantPromos() {
        return etudiantPromoRepository.findAll();
    }

    public List<EtudiantPromo> getEtudiantByPromotion(Long promotionId) {
        return etudiantPromoRepository.findByPromotionId(promotionId);
    }

    public List<EtudiantPromo> getEtudiantByCycle(Long cycleId) {
        return etudiantPromoRepository.findByCycleId(cycleId);
    }

    public List<EtudiantPromo> getEtudiantBySpecialite(Long specialiteId) {
        return etudiantPromoRepository.findBySpecialiteId(specialiteId);
    }

    public EtudiantPromo modifierEtudiantPromo(Long id, EtudiantPromo updatedPromo) {
        return etudiantPromoRepository.findById(id)
                .map(existingPromo -> {
                    existingPromo.setPromotion(updatedPromo.getPromotion());
                    existingPromo.setCycle(updatedPromo.getCycle());
                    existingPromo.setSpecialite(updatedPromo.getSpecialite());
                    return etudiantPromoRepository.save(existingPromo);
                })
                .orElseThrow(() -> new NoSuchElementException("Promotion non trouvée pour l'étudiant"));
    }

}

