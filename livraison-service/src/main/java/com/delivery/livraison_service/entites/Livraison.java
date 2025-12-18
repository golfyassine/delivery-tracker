package com.delivery.livraison_service.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String livreur;         // ex: "Karim"
    private String statutLivraison; // ex: "EN_COURS"
    private Long colisId;           // L'ID qui fait le lien avec l'autre service

    @Transient // Ce champ ne va pas en base de données, il sert juste à l'affichage
    private Colis colisInfo;
}