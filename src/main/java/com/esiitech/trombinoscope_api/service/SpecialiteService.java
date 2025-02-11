package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Specialite;
import com.esiitech.trombinoscope_api.repository.SpecialiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    public SpecialiteService(SpecialiteRepository specialiteRepository) {
        this.specialiteRepository = specialiteRepository;
    }

    public List<Specialite> getAllSpecialites() {
        return specialiteRepository.findAll();
    }

    public Optional<Specialite> getSpecialiteById(Long id) {
        return specialiteRepository.findById(id);
    }

    public Specialite createSpecialite(Specialite specialite) {
        return specialiteRepository.save(specialite);
    }

    public Specialite updateSpecialite(Long id, Specialite specialiteDetails) {
        return specialiteRepository.findById(id).map(specialite -> {
            specialite.setNom(specialiteDetails.getNom());
            return specialiteRepository.save(specialite);
        }).orElseThrow(() -> new RuntimeException("Spécialité non trouvée"));
    }

    public void deleteSpecialite(Long id) {
        specialiteRepository.deleteById(id);
    }
}
