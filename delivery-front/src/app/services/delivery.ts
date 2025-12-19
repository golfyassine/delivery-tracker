import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Livraison } from '../models/livraison.model'; // Assurez-vous que le chemin vers votre modèle est correct

@Injectable({
  providedIn: 'root'
})
export class DeliveryService { // J'ai renommé la classe en DeliveryService pour être standard

  // L'adresse de ta Gateway
  // Note: Dans le controller, le préfixe '/api' est utilisé pour certaines routes, mais '/suivi' et '/ajouter' sont à la racine
  // Il vaudrait mieux configurer une base URL et adapter selon l'endpoint
  private baseUrl = 'http://localhost:8888';

  constructor(private http: HttpClient) { }

  /**
   * Récupère une livraison par son ID (endpoint: /suivi/{id})
   */
  getLivraison(id: number): Observable<Livraison> {
    return this.http.get<Livraison>(`${this.baseUrl}/suivi/${id}`);
  }

  /**
   * Récupère toutes les livraisons pour le dashboard (endpoint: /api/livraisons)
   */
  getAllLivraisons(): Observable<Livraison[]> {
    return this.http.get<Livraison[]>(`${this.baseUrl}/api/livraisons`);
  }

  /**
   * Ajoute une nouvelle livraison (endpoint: /ajouter)
   */
  ajouterLivraison(livraison: Livraison): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.baseUrl}/ajouter`, livraison);
  }
}
