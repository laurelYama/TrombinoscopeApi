package com.esiitech.trombinoscope_api.mapper;

import com.esiitech.trombinoscope_api.DTOs.UtilisateurDto;
import com.esiitech.trombinoscope_api.Entity.Utilisateur;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {
    UtilisateurDto toDto(Utilisateur utilisateur);
    List<UtilisateurDto> toDtoList(List<Utilisateur> utilisateurs);

    Utilisateur toEntity(UtilisateurDto utilisateurDto); // facultatif, utile pour l'inverse
}
