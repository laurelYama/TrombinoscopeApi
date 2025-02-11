package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Parcours;
import com.esiitech.trombinoscope_api.repository.ParcoursRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;

    public ParcoursService(ParcoursRepository parcoursRepository) {
        this.parcoursRepository = parcoursRepository;
    }

    public List<Parcours> getAllParcours() {
        return parcoursRepository.findAll();
    }

    public Optional<Parcours> getParcoursById(Long id) {
        return parcoursRepository.findById(id);
    }

    public Parcours createParcours(Parcours parcours) {
        return parcoursRepository.save(parcours);
    }

    public Parcours updateParcours(Long id, Parcours parcoursDetails) {
        return parcoursRepository.findById(id).map(parcours -> {
            parcours.setNiveau(parcoursDetails.getNiveau());
            return parcoursRepository.save(parcours);
        }).orElseThrow(() -> new RuntimeException("Parcours non trouvé"));
    }

    public void deleteParcours(Long id) {
        parcoursRepository.deleteById(id);
    }
}

