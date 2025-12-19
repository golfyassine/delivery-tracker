import { Component, OnInit } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient, HttpHeaders } from '@angular/common/http';

registerLocaleData(localeFr);

// Import des interfaces depuis le modèle centralisé
import { Colis, Livraison } from '../../models/livraison.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {
  today = new Date();
  showModal = false;

  // Utilisation de l'interface typée
  livraisons: Livraison[] = [];

  // --- DONNÉES POUR LE SERVICE COLIS ---
  nouveauColisData: Partial<Colis> = {
    adresseDestination: '',
    adresseActuelle: 'Entrepôt Central',
    statut: 'EN_ATTENTE'
  };

  // --- DONNÉES POUR LE SERVICE LIVRAISON ---
  livreurNom: string = '';
  etatInitialLivraison: string = 'PREPARATION';

  // --- UI STATE ---
  isSubmitting = false;
  submissionMessage: string | null = null;
  submissionType: 'success' | 'error' | null = null;

  constructor(private router: Router, private http: HttpClient) { }

  ngOnInit(): void {
    this.chargerLivraisons();
  }

  // Récupère la liste fusionnée (Livraison + Infos Colis)
  chargerLivraisons() {
    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();

    this.http.get<Livraison[]>('http://localhost:8888/LIVRAISON-SERVICE/api/livraisons', { headers })
      .subscribe({
        next: (data) => {
          this.livraisons = data;
          console.log("Données chargées :", data);
        },
        error: (err) => console.error("Erreur chargement", err)
      });
  }

  ouvrirModal() {
    this.showModal = true;
    this.submissionMessage = null; // Reset messages
    this.isSubmitting = false;
  }
  fermerModal() { this.showModal = false; }

  // --- C'EST ICI QUE LA LOGIQUE S'OPÈRE ---
  enregistrerColis() {
    if (this.isSubmitting) return;

    this.isSubmitting = true;
    this.submissionMessage = null;
    this.submissionType = null;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    // 1. D'abord, on crée le COLIS
    const urlColis = 'http://localhost:8888/COLIS-SERVICE/api/colis/ajouter';

    this.http.post<Colis>(urlColis, this.nouveauColisData, { headers }).subscribe({
      next: (colisReponse) => {
        console.log("✅ Colis créé avec succès ! ID:", colisReponse.id);

        // 2. Ensuite, on crée la LIVRAISON avec l'ID du colis reçu
        const urlLivraison = 'http://localhost:8888/LIVRAISON-SERVICE/ajouter';

        const livraisonPayload = {
          colisId: colisReponse.id,
          livreur: this.livreurNom,
          statutLivraison: this.etatInitialLivraison,
          // On passe aussi l'objet colisInfo pour que le backend puisse le renvoyer
          // dans la réponse (astuce pour affichage immédiat sans recharger)
          colisInfo: colisReponse
        };

        console.log("📤 Envoi de la livraison:", JSON.stringify(livraisonPayload, null, 2));
        console.log("📤 ColisId:", livraisonPayload.colisId);
        console.log("📤 ColisInfo.id:", livraisonPayload.colisInfo?.id);

        this.http.post<Livraison>(urlLivraison, livraisonPayload, { headers }).subscribe({
          next: (livraisonResponse) => {
            this.submissionType = 'success';
            this.submissionMessage = `Succès ! Colis ${colisReponse.numeroSuivi} créé et livraison enregistrée.`;
            this.isSubmitting = false;

            // Fermer après un court délai pour lire le message
            setTimeout(() => {
              this.fermerModal();
              this.resetForm();
              this.chargerLivraisons();
            }, 1500);
          },
          error: (err) => {
            console.error('Erreur création livraison:', err);
            this.submissionType = 'error';
            
            // Gestion spécifique selon le code d'erreur
            if (err.status === 503) {
              this.submissionMessage = "⚠️ Service temporairement indisponible. Le colis a été créé (ID: " + colisReponse.id + ") mais la livraison n'a pas pu être enregistrée. Veuillez réessayer.";
            } else if (err.status === 400) {
              this.submissionMessage = "Erreur de validation. Veuillez vérifier les données saisies.";
            } else {
              this.submissionMessage = `Erreur lors de la création de la livraison (le colis ${colisReponse.numeroSuivi} a été créé avec succès).`;
            }
            
            this.isSubmitting = false;
          }
        });
      },
      error: (err) => {
        console.error(err);
        this.submissionType = 'error';
        this.submissionMessage = "Erreur technique : Impossible de créer le colis.";
        this.isSubmitting = false;
      }
    });
  }

  resetForm() {
    this.nouveauColisData = {
      adresseDestination: '',
      adresseActuelle: 'Entrepôt Central',
      statut: 'EN_ATTENTE'
    };
    this.livreurNom = '';
    this.etatInitialLivraison = 'PREPARATION';
  }

  viewDelivery(livraisonId: number | null | undefined, numeroSuivi?: string | null | undefined) {
    if (!livraisonId) return;
    // Navigation vers la page de recherche avec l'ID de la livraison en paramètre
    // La recherche se fera automatiquement sur la page de recherche
    this.router.navigate(['/recherche'], {
      queryParams: { id: livraisonId }
    });
    console.log("Voir détails pour la livraison ID:", livraisonId, "Numéro de suivi:", numeroSuivi);
  }

  goToSearch() { this.router.navigate(['/recherche']); }

  // Méthodes pour les statistiques
  getStatutCount(statut: string): number {
    return this.livraisons.filter(liv => liv.statutLivraison === statut).length;
  }

  // Méthode pour obtenir la classe CSS du badge selon le statut
  getBadgeClass(statut?: string | null): string {
    if (!statut) return 'badge-pending';
    switch (statut.toUpperCase()) {
      case 'LIVRE':
        return 'badge-success';
      case 'EN_ATTENTE':
        return 'badge-pending';
      case 'EN_COURS':
        return 'badge-progress';
      default:
        return 'badge-pending';
    }
  }

  // Méthode pour obtenir la classe CSS du dot selon le statut de livraison
  getDotClass(statut?: string | null): string {
    if (!statut) return 'dot-orange';
    switch (statut.toUpperCase()) {
      case 'EN_ROUTE':
        return 'dot-green';
      case 'LIVRE':
        return 'dot-blue';
      case 'INCIDENT':
        return 'dot-red';
      case 'PREPARATION':
      default:
        return 'dot-orange';
    }
  }
}
