package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    boolean existsByAnnee(int annee);
}
