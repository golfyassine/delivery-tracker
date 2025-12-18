import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet], // On importe uniquement le RouterOutlet
  templateUrl: './app.html'
})
export class App {
  // Ce composant est maintenant vide, il ne sert qu'à afficher les autres
}
