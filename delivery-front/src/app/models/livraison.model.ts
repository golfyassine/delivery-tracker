// src/app/models/livraison.model.ts

/**
 * Interface Colis - Correspond exactement à l'entité Java backend
 * Tous les champs sont optionnels sauf ceux marqués comme requis par le backend
 */
export interface Colis {
  id?: number | null;
  numeroSuivi?: string | null;
  adresseActuelle?: string | null;
  adresseDestination?: string | null;
  statut?: 'EN_ATTENTE' | 'EN_COURS' | 'LIVRE' | string | null;
  // Coordonnées GPS (optionnelles car calculées après)
  latitude?: number | null;
  longitude?: number | null;
  latitudeDest?: number | null;
  longitudeDest?: number | null;
}

/**
 * Interface Livraison - Correspond exactement à l'entité Java backend
 * Le champ colisInfo est transient côté backend mais présent dans le JSON
 */
export interface Livraison {
  id: number;
  livreur: string;
  statutLivraison: 'PREPARATION' | 'EN_ROUTE' | 'LIVRE' | 'INCIDENT' | string;
  colisId: number | null;
  // Le champ "transient" qui contient les infos imbriquées du colis
  // Peut être null si le service Colis est indisponible (Circuit Breaker)
  colisInfo?: Colis | null;
}

/**
 * Type pour les statuts de livraison (pour la validation TypeScript)
 */
export type StatutLivraison = 'PREPARATION' | 'EN_ROUTE' | 'LIVRE' | 'INCIDENT';

/**
 * Type pour les statuts de colis (pour la validation TypeScript)
 */
export type StatutColis = 'EN_ATTENTE' | 'EN_COURS' | 'LIVRE';
