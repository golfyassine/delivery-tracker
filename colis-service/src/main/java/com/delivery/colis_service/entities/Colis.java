package com.delivery.colis_service.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Colis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroSuivi;
    private String adresseDestination;
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

    // --- AUTOMATISATION (Magie JPA) ---
    // Cette méthode s'exécute juste avant l'enregistrement en base

    @PrePersist
    public void initialiserDonnees() {
        // 1. Génération du numéro de suivi s'il est vide
        if (this.numeroSuivi == null || this.numeroSuivi.isEmpty()) {
            this.numeroSuivi = "COL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        // 2. Statut par défaut
        if (this.statut == null) {
            this.statut = "EN_ATTENTE";
        }
        // 3. Adresse actuelle par défaut
        if (this.adresseActuelle == null) {
            this.adresseActuelle = "Entrepôt Central";
        }
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroSuivi() { return numeroSuivi; }
    public void setNumeroSuivi(String numeroSuivi) { this.numeroSuivi = numeroSuivi; }

    public String getAdresseActuelle() { return adresseActuelle; }
    public void setAdresseActuelle(String adresseActuelle) { this.adresseActuelle = adresseActuelle; }

    public String getAdresseDestination() { return adresseDestination; }
    public void setAdresseDestination(String adresseDestination) { this.adresseDestination = adresseDestination; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}