package com.delivery.livraison_service.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeoService {

    private final RestClient restClient;

    public GeoService() {
        // Remplacement de WebClient par RestClient (compatible Spring Boot 3.4 sans WebFlux)
        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .build();
    }

    public Map<String, Double> getCoordonnees(String adresse) {
        try {
            // Appel API simplifié avec RestClient
            List<Map<String, Object>> response = this.restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", adresse)
                            .queryParam("format", "json")
                            .queryParam("limit", "1")
                            .build())
                    .retrieve()
                    // Plus besoin de .block(), RestClient est synchrone par défaut
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            if (response != null && !response.isEmpty()) {
                Map<String, Object> premierResultat = response.get(0);
                return Map.of(
                        "lat", Double.parseDouble(premierResultat.get("lat").toString()),
                        "lon", Double.parseDouble(premierResultat.get("lon").toString())
                );
            }
        } catch (Exception e) {
            System.err.println("Erreur de géolocalisation : " + e.getMessage());
        }
        return null; // Adresse non trouvée
    }
}