package com.delivery.livraison_service.client;

import com.delivery.livraison_service.entites.Colis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Interface Feign Client pour communiquer avec le service Colis
 * Le Circuit Breaker est configuré automatiquement via application.yml
 * Le fallback est géré par la classe ColisRestClientFallback
 */
@FeignClient(name = "COLIS-SERVICE", fallback = ColisRestClientFallback.class)
public interface ColisRestClient {

    /**
     * Récupère un colis par son ID
     * Le Circuit Breaker et Time Limiter sont configurés dans application.yml
     * En cas d'échec, le fallback retourne un Colis avec des valeurs par défaut
     * 
     * @param id L'ID du colis à récupérer
     * @return Le Colis correspondant ou un fallback si le service est indisponible
     */
    @GetMapping("/colis/{id}")
    Colis getColisById(@PathVariable("id") Long id);

    /**
     * Crée un nouveau colis
     * Le Circuit Breaker et Time Limiter sont configurés dans application.yml
     * En cas d'échec, le fallback retourne null
     * 
     * @param colis Le colis à créer
     * @return Le Colis créé avec son ID généré, ou null si le service est indisponible
     */
    @PostMapping("/colis")
    Colis createColis(@RequestBody Colis colis);
}