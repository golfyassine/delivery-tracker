package com.delivery.livraison_service.web;

import com.delivery.livraison_service.client.ColisRestClient;
import com.delivery.livraison_service.entites.Colis;
import com.delivery.livraison_service.entites.Livraison;
import com.delivery.livraison_service.repository.LivraisonRepository;
import com.delivery.livraison_service.services.GeoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class LivraisonController {

    private final LivraisonRepository livraisonRepository;
    private final ColisRestClient colisRestClient;
    private final GeoService geoService;

    public LivraisonController(LivraisonRepository livraisonRepository, ColisRestClient colisRestClient, GeoService geoService) {
        this.livraisonRepository = livraisonRepository;
        this.colisRestClient = colisRestClient;
        this.geoService = geoService;

        // Données de test au démarrage (optionnel)
        if (livraisonRepository.count() == 0) {
            Livraison l1 = new Livraison();
            l1.setLivreur("Karim Benz");
            l1.setStatutLivraison("EN_ROUTE");
            l1.setColisId(1L);
            livraisonRepository.save(l1);
        }
    }

    // --- LISTE POUR LE DASHBOARD ---
    @GetMapping("/api/livraisons")
    public List<Livraison> getAllLivraisons() {
        List<Livraison> livraisons = livraisonRepository.findAll();
        return livraisons.stream().map(liv -> {
            if (liv.getColisId() != null) {
                // Le Circuit Breaker gère automatiquement les erreurs
                // Le fallback retournera un Colis avec des valeurs par défaut si le service est down
                Colis colis = colisRestClient.getColisById(liv.getColisId());
                if (colis != null) {
                    liv.setColisInfo(colis);
                }
            }
            return liv;
        }).collect(Collectors.toList());
    }

    // --- CRÉATION INTELLIGENTE (Livraison + Colis) ---
    @PostMapping("/ajouter")
    public ResponseEntity<Livraison> ajouterLivraison(@RequestBody Livraison livraison) {
        
        // Log de débogage pour voir ce qui est reçu
        System.out.println("📥 Requête reçue - Livreur: " + livraison.getLivreur());
        System.out.println("📥 Statut: " + livraison.getStatutLivraison());
        System.out.println("📥 ColisId direct: " + livraison.getColisId());
        System.out.println("📥 ColisInfo: " + (livraison.getColisInfo() != null ? "présent" : "null"));
        if (livraison.getColisInfo() != null) {
            System.out.println("📥 ColisInfo.id: " + livraison.getColisInfo().getId());
            System.out.println("📥 ColisInfo.numeroSuivi: " + livraison.getColisInfo().getNumeroSuivi());
        }

        // 1. Gestion du colisId
        // Le frontend peut envoyer soit :
        // - colisId directement dans la requête (recommandé)
        // - colisInfo avec un ID déjà créé (cas actuel : frontend crée d'abord le colis)
        // - colisInfo sans ID (création côté backend - fallback)
        
        // Priorité 1 : Si colisId est déjà défini directement, on l'utilise
        if (livraison.getColisId() != null) {
            System.out.println("✅ Utilisation du colisId direct: " + livraison.getColisId());
        }
        // Priorité 2 : Si colisInfo contient un ID, on l'utilise
        else if (livraison.getColisInfo() != null && livraison.getColisInfo().getId() != null) {
            livraison.setColisId(livraison.getColisInfo().getId());
            System.out.println("✅ Utilisation du colis existant depuis colisInfo ID: " + livraison.getColisInfo().getId());
        } 
        // Priorité 3 : Sinon, on essaie de créer le colis via le service (fallback si frontend ne le fait pas)
        else if (livraison.getColisInfo() != null) {
            System.out.println("⚠️ Tentative de création du colis via le service...");
            Colis nouveauColis = colisRestClient.createColis(livraison.getColisInfo());
            
            // Vérifier si le fallback a été utilisé (retourne null)
            if (nouveauColis == null || nouveauColis.getId() == null) {
                System.err.println("❌ Erreur : Service Colis indisponible - Impossible de créer le colis");
                return ResponseEntity.status(503)
                    .header("X-Service-Status", "COLIS-SERVICE-UNAVAILABLE")
                    .build();
            }
            
            // On récupère l'ID généré et on l'associe à la livraison
            livraison.setColisId(nouveauColis.getId());
            // Mettre à jour colisInfo avec l'ID généré
            livraison.getColisInfo().setId(nouveauColis.getId());
            System.out.println("✅ Colis créé avec succès ID: " + nouveauColis.getId());
        }
        
        // Vérifier que colisId est défini avant de sauvegarder
        if (livraison.getColisId() == null) {
            System.err.println("❌ Erreur : colisId est null - Impossible de créer la livraison");
            System.err.println("   Livreur: " + livraison.getLivreur());
            System.err.println("   ColisInfo: " + (livraison.getColisInfo() != null ? "présent" : "null"));
            return ResponseEntity.badRequest()
                .header("X-Error", "MISSING_COLIS_ID")
                .build();
        }

        // 2. Ensuite, on sauvegarde la Livraison locale
        Livraison savedLivraison = livraisonRepository.save(livraison);

        // On remet l'objet colisInfo pour que le Frontend puisse l'afficher directement
        savedLivraison.setColisInfo(livraison.getColisInfo());

        return ResponseEntity.ok(savedLivraison);
    }

    // --- DÉTAIL POUR LA RECHERCHE (Avec Enrichissement GPS) ---
    @GetMapping("/suivi/{id}")
    public Livraison getLivraison(@PathVariable Long id) {
        Livraison livraison = livraisonRepository.findById(id).orElse(null);

        if (livraison != null && livraison.getColisId() != null) {
            // Le Circuit Breaker de Resilience4j gère automatiquement les erreurs
            // via les annotations @CircuitBreaker sur l'interface Feign
            Colis colis = colisRestClient.getColisById(livraison.getColisId());
            
            // Vérifier si le fallback a été utilisé (indicateur : numeroSuivi commence par "SERVICE_UNAVAILABLE")
            if (colis != null) {
                boolean isFallback = colis.getNumeroSuivi() != null && 
                                     colis.getNumeroSuivi().startsWith("SERVICE_UNAVAILABLE");
                
                if (!isFallback) {
                    // Seulement enrichir si ce n'est pas un fallback
                    enrichirGeolocalisation(colis);
                } else {
                    // Logger l'utilisation du fallback pour monitoring
                    System.out.println("⚠️ Fallback utilisé pour Colis ID: " + livraison.getColisId());
                }
                
                livraison.setColisInfo(colis);
            }
        }
        return livraison;
    }

    private void enrichirGeolocalisation(Colis colis) {
        if (colis.getAdresseActuelle() != null) {
            Map<String, Double> coords = geoService.getCoordonnees(colis.getAdresseActuelle());
            if (coords != null) {
                colis.setLatitude(coords.get("lat"));
                colis.setLongitude(coords.get("lon"));
            }
        }
        if (colis.getAdresseDestination() != null) {
            Map<String, Double> coordsDest = geoService.getCoordonnees(colis.getAdresseDestination());
            if (coordsDest != null) {
                colis.setLatitudeDest(coordsDest.get("lat"));
                colis.setLongitudeDest(coordsDest.get("lon"));
            }
        }
    }
}