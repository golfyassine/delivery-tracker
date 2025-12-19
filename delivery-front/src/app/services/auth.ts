import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8888/auth'; // On passe par la Gateway !

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        // On enregistre le token et le nom d'utilisateur
        // Le backend retourne 'token' et 'username' selon AuthResponse
        const token = res.token || res.accessToken; // Support des deux formats
        if (token) {
          localStorage.setItem('token', token);
          localStorage.setItem('username', res.username || credentials.username);
        } else {
          console.error('Token non reçu dans la réponse:', res);
        }
      })
    );
  }

  getToken() {
    return localStorage.getItem('token');
  }

  isLoggedIn() {
    return !!localStorage.getItem('token');
  }

  logout() {
    localStorage.clear();
    window.location.reload(); // Pour vider l'état de l'app
  }
}
