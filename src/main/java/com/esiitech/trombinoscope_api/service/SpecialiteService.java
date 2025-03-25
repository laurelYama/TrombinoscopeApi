package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.Entity.Specialite;
import com.esiitech.trombinoscope_api.Exception.SpecialiteNotFoundException;
import com.esiitech.trombinoscope_api.repository.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    public List<Specialite> getAllSpecialites() {
        return specialiteRepository.findAll();
    }

    public Specialite createSpecialite(Specialite specialite) {
        if (specialiteRepository.existsByNom(specialite.getNom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette spécialité existe déjà !");
        }
        return specialiteRepository.save(specialite);
    }

    public Specialite getSpecialiteById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException("Spécialité avec ID " + id + " non trouvée"));
    }

    public Specialite updateSpecialite(Long id, Specialite newSpecialite) {
        return specialiteRepository.findById(id).map(specialite -> {
            if (!specialite.getNom().equals(newSpecialite.getNom()) && specialiteRepository.existsByNom(newSpecialite.getNom())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une autre spécialité avec ce nom existe déjà !");
            }
            specialite.setNom(newSpecialite.getNom());
            return specialiteRepository.save(specialite);
        }).orElseThrow(() -> new SpecialiteNotFoundException("Spécialité avec ID " + id + " non trouvée"));
    }
}
