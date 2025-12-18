package com.delivery.colis_service.repository;

import com.delivery.colis_service.entities.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
// 👇 IMPORT IMPORTANT : On change l'import !
import org.springframework.data.repository.query.Param;

@RepositoryRestResource(path = "colis")
public interface ColisRepository extends JpaRepository<Colis, Long> {

    // 👇 CORRECTION : On utilise @Param ici
    Colis findByNumeroSuivi(@Param("numero") String numeroSuivi);

}