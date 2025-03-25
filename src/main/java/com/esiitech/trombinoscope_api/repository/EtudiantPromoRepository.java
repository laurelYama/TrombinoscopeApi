package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.EtudiantPromo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EtudiantPromoRepository extends JpaRepository<EtudiantPromo, Long> {
    List<EtudiantPromo> findByPromotionId(Long promotionId);
    List<EtudiantPromo> findByCycleId(Long cycleId);
    List<EtudiantPromo> findBySpecialiteId(Long specialiteId);
    List<EtudiantPromo> findByEtudiantId(Long etudiantId);
    List<EtudiantPromo> findByPromotionIdAndCycleIdAndSpecialiteId(Long promotionId, Long cycleId, Long specialiteId);

}

