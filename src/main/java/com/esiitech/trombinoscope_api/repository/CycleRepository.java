package com.esiitech.trombinoscope_api.repository;


import com.esiitech.trombinoscope_api.Entity.Cycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, Long> {
}
