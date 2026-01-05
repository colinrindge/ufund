import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { User } from '../user';
import { Session } from '../session';
import { SessionService } from '../session-service';
import { UserService } from '../user-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  username: string = '';
  password: string = '';
  errorMessage: string = '';
  private router = inject(Router);

  constructor(private sessionService: SessionService, private userService: UserService) {}

  onLogin(): void {
    if (!this.username || !this.password) {
      this.errorMessage = 'Please enter username and password';
      return;
    }

    this.sessionService.login(this.username, this.password).subscribe({
      next: (session) => {
        if (session) {
          this.userService.getUser(this.username).subscribe((data) => {
            if (data.restricted == true) {
              this.errorMessage = 'Unable to login, user is restricted';
              this.sessionService.deleteSession(this.username);
              return;
            }

            this.userService.getCurrentUser().subscribe((data) => {
              data.userName = session.userName;
              data.id = session.id;
            });
            if (!document.cookie) {
              document.cookie = `session_id=${session.id}`;
            }
            this.router.navigate(['/home']);
          });
        } else {
          this.errorMessage = 'Invalid credentials. Please try again or create an account';
        }
      },
    });
  }

  createAccount(): void {
    this.router.navigate(['/create']);
  }

  forgotPassword(): void {
    this.router.navigate(['/forgot']);
  }
  
  continueWithoutAccount(): void {
    this.router.navigate(['/home']);
  }
}
