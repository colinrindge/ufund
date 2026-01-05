import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './header/header';
import { User } from './user'
import { Need } from './need'

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css',
})
export class App {
  currentUser: User = {
    id: 0,
    userName: 'Guest',
    basket: [],
    password: '',
    restricted: false,
    security: []
  };
  needs: Need[] = [];

  protected readonly title = signal('ufund-ui');
}
