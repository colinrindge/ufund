import { Component, Input } from '@angular/core';
import { User } from '../user';

@Component({
  selector: 'app-bee-icon',
  standalone: false,
  templateUrl: './bee-icon.html',
  styleUrl: './bee-icon.css',
})
export class BeeIcon {
  @Input() currentUser!: User;
  showChat: boolean = false;

  switchView() {
    this.showChat = !this.showChat;
  }
}
