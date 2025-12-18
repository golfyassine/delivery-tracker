package com.delivery.livraison_service.client;

import com.delivery.livraison_service.entites.Colis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Le nom doit correspondre à celui dans Eureka (souvent en majuscules par défaut)
@FeignClient(name = "COLIS-SERVICE")
public interface ColisRestClient {

    // On remplace la méthode par celle attendue par le contrôleur
    // Elle prend un Long (l'ID) et non un String (le numéro)
    @GetMapping("/colis/{id}")
    Colis getColisById(@PathVariable("id") Long id);

}