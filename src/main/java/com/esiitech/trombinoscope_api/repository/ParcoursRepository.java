package com.esiitech.trombinoscope_api.repository;


import com.esiitech.trombinoscope_api.Entity.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcoursRepository extends JpaRepository<Parcours, Long> {
}
