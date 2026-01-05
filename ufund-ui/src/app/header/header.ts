import { Component, inject, Input, OnInit } from '@angular/core';
import { SessionService } from '../session-service';
import { UserService } from '../user-service';
import { User } from '../user';
import { BasketNeed } from '../basketNeed';
import { Need } from '../need';
import { Session } from '../session';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  constructor(private sessionService: SessionService, private userService: UserService) {}
  private router = inject(Router);
  isDropdownVisible = true; // username input box
  isSignOutVisible = false; // sign out button visibility
  showLogin: boolean = false;

  isEditing = false;
  isEditingAdmin = false;

  text = 'Sign Out'; // Sign Out button text
  @Input() currentUser!: User;
  @Input() needs!: Need[];

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe((data) => (this.currentUser = data));
    if (document.cookie) {
      let sessionId = Number(document.cookie.split('=')[1]);
      this.userService.getUserByID(sessionId).subscribe((userData) => {
        this.sessionService.validateSession(userData.userName).subscribe((session) => {
          if (session) {
            this.currentUser.userName = userData.userName;
            this.currentUser.id = userData.id;
            this.router.navigate(['/home']);
          }
        });
      });
    } else {
      console.log('user does not exist or session is invalid');
    }
  }

  onSignOutClick(): void {
    try {
      this.closeBasket();
      this.closeEditUser();
      this.sessionService.deleteSession(this.currentUser.userName).subscribe(() => {
        this.currentUser.userName = 'Guest';
        this.currentUser.id = -1;
        this.isSignOutVisible = false;
        this.isDropdownVisible = true;
        document.cookie = 'session_id=; expires=Mon, 01 Jan 2000 00:00:00 UTC;';
        this.router.navigate(['/']);
      });
    } catch (error) {
      return;
    }
  }

  viewBasket = false;
  basket: BasketNeed[] = [];

  openBasket(): void {
    if (this.currentUser.id) {
      this.userService.getBasket(this.currentUser.id).subscribe((basket) => {
        this.basket = basket;
        this.viewBasket = true;
      });
    }
  }

  closeBasket(): void {
    if (this.currentUser.userName != '') {
      this.viewBasket = false;
    }
  }

  openEditUser() {
    if(this.currentUser.userName == "admin"){
      this.isEditingAdmin = !this.isEditingAdmin;
    } else {
      this.isEditing = !this.isEditing;
    }
  }

  closeEditUser() {
    this.isEditing = false;
    this.isEditingAdmin = false;
  }

}
