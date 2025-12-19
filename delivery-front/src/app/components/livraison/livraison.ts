import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LivraisonService {
  // On passe par la Gateway (8888) et le nom du service enregistré sur Eureka
  private apiUrl = 'http://localhost:8888/LIVRAISON-SERVICE/api/livraisons';

  constructor(private http: HttpClient) {}

  // Récupérer toutes les livraisons pour le dashboard
  getLivraisons(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // Créer une nouvelle livraison
  creerLivraison(data: any): Observable<any> {
    // Note : On utilise l'URL de base car souvent le POST est sur la racine /api/livraisons
    return this.http.post<any>(this.apiUrl, data);
  }

  // Optionnel : Récupérer une livraison par son ID (pour la recherche)
  getLivraisonById(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}
