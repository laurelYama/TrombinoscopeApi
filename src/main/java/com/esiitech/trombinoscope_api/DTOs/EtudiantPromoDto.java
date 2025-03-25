package com.esiitech.trombinoscope_api.DTOs;

import com.esiitech.trombinoscope_api.Entity.EtudiantPromo;
import lombok.Data;

@Data
public class EtudiantPromoDto {
    private Long id;
    private String cycle;
    private int promotion;
    private String specialite;

    public EtudiantPromoDto(EtudiantPromo etudiantPromo) {
        this.id = etudiantPromo.getId();
        this.cycle = etudiantPromo.getCycle().getNiveau();
        this.promotion = etudiantPromo.getPromotion().getAnnee();
        this.specialite = etudiantPromo.getSpecialite().getNom();
    }
}

