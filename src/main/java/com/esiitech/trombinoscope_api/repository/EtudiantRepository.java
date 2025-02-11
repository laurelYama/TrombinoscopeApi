package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {


        List<Etudiant> findByPromotionId(Long promotionId);

        List<Etudiant> findByParcoursId(Long parcoursId);

        List<Etudiant> findBySpecialiteId(Long specialiteId);

        List<Etudiant> findByPromotionIdAndParcoursIdAndSpecialiteId(Long promotionId, Long parcoursId, Long specialiteId);

}
