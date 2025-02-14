package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    boolean existsByNom(String nom);
}
