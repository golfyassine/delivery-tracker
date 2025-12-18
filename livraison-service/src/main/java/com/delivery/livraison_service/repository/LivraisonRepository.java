package com.delivery.livraison_service.repository;

import com.delivery.livraison_service.entites.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

// C'est une interface, pas une classe !
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    // JpaRepository nous donne automatiquement findAll(), findById(), save(), etc.
}