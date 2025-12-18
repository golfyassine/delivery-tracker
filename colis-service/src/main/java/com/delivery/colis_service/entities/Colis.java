package com.delivery.colis_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Colis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String adresseDestination;
    private String numeroSuivi;
    private String adresseActuelle;
    private String statut;

    // --- CONSTRUCTEURS ---

    public Colis() {
    }

    public Colis(Long id, String numeroSuivi, String adresseActuelle, String adresseDestination, String statut) {
        this.id = id;
        this.numeroSuivi = numeroSuivi;
        this.adresseDestination = adresseDestination;
        this.adresseActuelle = adresseActuelle;
        this.statut = statut;
    }

    // --- GETTERS & SETTERS (C'est ce qui manquait pour le JSON !) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroSuivi() {
        return numeroSuivi;
    }

    public void setNumeroSuivi(String numeroSuivi) {
        this.numeroSuivi = numeroSuivi;
    }

    public String getAdresseActuelle() {
        return adresseActuelle;
    }

    public void setAdresseActuelle(String adresseActuelle) {
        this.adresseActuelle = adresseActuelle;
    }

    public String getAdresseDestination() {
        return adresseDestination;
    }

    public void setAdresseDestination(String adresseDestination) {
        this.adresseDestination = adresseDestination;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}