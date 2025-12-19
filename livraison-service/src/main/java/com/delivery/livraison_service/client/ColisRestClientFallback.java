package com.delivery.livraison_service.client;

import com.delivery.livraison_service.entites.Colis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback amélioré pour le service Colis avec gestion d'erreurs robuste
 */
@Component
public class ColisRestClientFallback implements ColisRestClient {

    private static final Logger logger = LoggerFactory.getLogger(ColisRestClientFallback.class);
    private static final ColisRestClientFallback INSTANCE = new ColisRestClientFallback();

    // Singleton pour être utilisé dans les méthodes default de l'interface
    public static ColisRestClientFallback getInstance() {
        return INSTANCE;
    }

    @Override
    public Colis getColisById(Long id) {
        logger.warn("⚠️ Circuit Breaker activé : Service Colis indisponible pour l'ID {}", id);
        
        // Fallback amélioré avec informations cohérentes
        Colis fallback = new Colis();
        fallback.setId(id);
        fallback.setNumeroSuivi("SERVICE_UNAVAILABLE_" + id);
        fallback.setAdresseDestination("Service temporairement indisponible");
        fallback.setAdresseActuelle("Service indisponible");
        fallback.setStatut("EN_ATTENTE");
        
        logger.info("✅ Fallback retourné pour Colis ID: {}", id);
        return fallback;
    }

    @Override
    public Colis createColis(Colis colis) {
        logger.error("❌ Circuit Breaker activé : Impossible de créer le colis - Service Colis indisponible");
        logger.error("Données du colis non créé : {}", colis);
        
        // Pour la création, on retourne null pour que le contrôleur puisse gérer l'erreur
        // Le contrôleur vérifiera null et retournera une erreur HTTP appropriée
        return null;
    }
}
