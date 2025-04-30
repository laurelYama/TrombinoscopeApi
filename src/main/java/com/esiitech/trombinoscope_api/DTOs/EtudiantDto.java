package com.esiitech.trombinoscope_api.DTOs;

import com.esiitech.trombinoscope_api.Entity.Etudiant;
import com.esiitech.trombinoscope_api.Entity.EtudiantPromo;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class EtudiantDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String nationalite;
    private boolean actif;
    private List<EtudiantPromoDto> promotions;

    // Constructeur attendu
    public EtudiantDto(Etudiant etudiant, List<EtudiantPromo> etudiantPromos) {
        this.id = etudiant.getId();
        this.nom = etudiant.getNom();
        this.prenom = etudiant.getPrenom();
        this.email = etudiant.getEmail();
        this.telephone = etudiant.getTelephone();
        this.nationalite = etudiant.getNationalite();
        this.actif = etudiant.isActif();

        // Convertir les promotions si nécessaire
        this.promotions = etudiantPromos.stream()
                .map(EtudiantPromoDto::new) // Assurez-vous que `EtudiantPromoDto` a un constructeur `EtudiantPromoDto(EtudiantPromo)`
                .collect(Collectors.toList());
    }

    // Méthode fromEntity pour convertir un Etudiant en EtudiantDto
    public static EtudiantDto fromEntity(Etudiant etudiant, List<EtudiantPromo> etudiantPromos) {
        return new EtudiantDto(etudiant, etudiantPromos);
    }
}
