package com.esiitech.trombinoscope_api.service;

import com.esiitech.trombinoscope_api.DTOs.DiplomeRequest;
import com.esiitech.trombinoscope_api.Entity.Diplome;
import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.repository.DiplomeRepository;
import com.esiitech.trombinoscope_api.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class DiplomeService {

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    // 📌 Créer un diplôme avec un numéro unique
    public Diplome createDiplome(DiplomeRequest request) {
        // Vérifier si le numéro de diplôme existe déjà
        if (diplomeRepository.existsByNumero(request.getNumero())) {
            throw new IllegalArgumentException("Ce numéro de diplôme existe déjà.");
        }

        Etudiant etudiant = etudiantRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new IllegalArgumentException("Étudiant introuvable"));

        Diplome diplome = new Diplome();
        diplome.setNumero(request.getNumero());
        diplome.setNom(request.getNom());
        diplome.setEtudiant(etudiant);

        return diplomeRepository.save(diplome);
    }

    // 📌 Récupérer un diplôme par son numéro
    public Diplome getDiplomeByNumero(String numero) {
        return diplomeRepository.findByNumero(numero)
                .orElseThrow(() -> new NoSuchElementException("Diplôme introuvable"));
    }

    // 📌 Supprimer un diplôme
    public void deleteDiplome(String numero) {
        Diplome diplome = getDiplomeByNumero(numero);
        diplomeRepository.delete(diplome);
    }

    // 📌 Mettre à jour un diplôme
    public Diplome updateDiplome(String numero, DiplomeRequest request) {
        Diplome diplome = getDiplomeByNumero(numero);
        diplome.setNom(request.getNom());
        return diplomeRepository.save(diplome);
    }
}
