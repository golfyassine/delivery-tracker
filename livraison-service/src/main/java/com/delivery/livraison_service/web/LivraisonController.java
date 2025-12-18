package com.delivery.livraison_service.web;

import com.delivery.livraison_service.client.ColisRestClient;
import com.delivery.livraison_service.entites.Colis;
import com.delivery.livraison_service.entites.Livraison;
import com.delivery.livraison_service.repository.LivraisonRepository;
import com.delivery.livraison_service.services.GeoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LivraisonController {

    private final LivraisonRepository livraisonRepository; // <--- Indispensable pour avoir le livreur
    private final ColisRestClient colisRestClient;
    private final GeoService geoService;

    public LivraisonController(LivraisonRepository livraisonRepository, ColisRestClient colisRestClient, GeoService geoService) {
        this.livraisonRepository = livraisonRepository;
        this.colisRestClient = colisRestClient;
        this.geoService = geoService;

        // --- Données de test (Important car H2 se vide au redémarrage) ---
        if (livraisonRepository.count() == 0) {
            Livraison l1 = new Livraison();
            l1.setLivreur("Karim");
            l1.setStatutLivraison("EN_ROUTE");
            l1.setColisId(1L); // <--- On dit que cette livraison concerne le Colis ID 1
            livraisonRepository.save(l1);
            System.out.println("✅ Livraison de test créée pour le colis 1");
        }
    }

    @GetMapping("/suivi/{id}")
    public Livraison getLivraison(@PathVariable Long id) {
        // 1. On cherche la Livraison
        Livraison livraison = livraisonRepository.findById(id).orElse(null);

        if (livraison != null && livraison.getColisId() != null) {
            // 2. Appel au microservice Colis
            Colis colis = colisRestClient.getColisById(livraison.getColisId());

            if (colis != null) {
                // --- A. Géolocalisation de la position ACTUELLE ---
                if (colis.getAdresseActuelle() != null) {
                    Map<String, Double> coords = geoService.getCoordonnees(colis.getAdresseActuelle());
                    if (coords != null) {
                        colis.setLatitude(coords.get("lat"));
                        colis.setLongitude(coords.get("lon"));
                    }
                }

                // --- B. Géolocalisation de la DESTINATION (C'est ce qu'il manquait !) ---
                if (colis.getAdresseDestination() != null) {
                    Map<String, Double> coordsDest = geoService.getCoordonnees(colis.getAdresseDestination());
                    if (coordsDest != null) {
                        colis.setLatitudeDest(coordsDest.get("lat"));
                        colis.setLongitudeDest(coordsDest.get("lon"));
                    }
                }

                // 3. Fusion finale
                livraison.setColisInfo(colis);
            }
        }
        return livraison;
    }
}