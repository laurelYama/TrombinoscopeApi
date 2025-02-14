package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    List<Etudiant> findByPromotionId(Long promotionId);
    List<Etudiant> findByParcoursId(Long parcoursId);
    List<Etudiant> findBySpecialiteId(Long specialiteId);
    List<Etudiant> findByPromotionIdAndParcoursIdAndSpecialiteId(Long promotionId, Long parcoursId, Long specialiteId);

    // Pagination pour éviter de renvoyer une liste trop volumineuse
    Page<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);



}
