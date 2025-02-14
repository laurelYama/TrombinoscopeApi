package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Parcours;
import com.esiitech.trombinoscope_api.repository.ParcoursRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ParcoursService {

    private final ParcoursRepository parcoursRepository;

    public ParcoursService(ParcoursRepository parcoursRepository) {
        this.parcoursRepository = parcoursRepository;
    }

    public List<Parcours> getAllParcours() {
        return parcoursRepository.findAll();
    }

    public Parcours getParcoursById(Long id) {
        return parcoursRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcours non trouvé"));
    }

    public Parcours createParcours(Parcours parcours) {
        return parcoursRepository.save(parcours);
    }

    public Parcours updateParcours(Long id, Parcours newParcours) {
        return parcoursRepository.findById(id).map(parcours -> {
            parcours.setNiveau(newParcours.getNiveau());
            return parcoursRepository.save(parcours);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcours non trouvé"));
    }

    public void deleteParcours(Long id) {
        if (!parcoursRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parcours non trouvé");
        }
        parcoursRepository.deleteById(id);
    }
}
