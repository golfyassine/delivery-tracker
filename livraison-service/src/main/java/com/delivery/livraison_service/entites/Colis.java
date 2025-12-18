package com.delivery.livraison_service.entites; // <--- Package entities

import lombok.Data;

@Data
public class Colis { // <--- Nom identique à l'autre service
    private String numeroSuivi;
    private String adresseActuelle;
    private String adresseDestination;
    private String statut;
    private Double latitude;
    private Double longitude;
    private Double latitudeDest;
    private Double longitudeDest;
}