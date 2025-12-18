import { Component } from '@angular/core';
import { AuthService } from '../../services/auth';import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; // <--- C'est cette ligne qui manque !

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/recherche']); // Redirige vers la recherche après succès
      },
      error: (err) => {
        this.errorMessage = "Identifiants invalides ou serveur indisponible";
      }
    });
  }
}
