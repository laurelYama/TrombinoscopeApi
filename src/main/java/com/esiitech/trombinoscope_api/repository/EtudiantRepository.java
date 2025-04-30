package com.esiitech.trombinoscope_api.repository;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    List<Etudiant> findByActifTrue();

}
