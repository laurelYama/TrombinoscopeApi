package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.Diplome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiplomeRepository extends JpaRepository<Diplome, Long> {
    boolean existsByNumero(String numero);
    Optional<Diplome> findByNumero(String numero);
}



