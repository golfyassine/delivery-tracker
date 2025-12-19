package com.delivery.livraison_service.entites;

import lombok.Data;

@Data // <--- Génère automatiquement getId(), setId(), toString(), etc.
public class Colis {

    // 1. L'ID est indispensable pour récupérer la réponse du microservice Colis
    private Long id;

    // 2. Les autres champs nécessaires
    private String numeroSuivi;
    private String adresseActuelle;
    private String adresseDestination;
    private String statut;

    // 3. Les coordonnées GPS (pour la carte)
    private Double latitude;
    private Double longitude;
    private Double latitudeDest;
    private Double longitudeDest;
}