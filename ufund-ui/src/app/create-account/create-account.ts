import { Component, inject } from '@angular/core';
import { UserService } from '../user-service';
import { SessionService } from '../session-service';
import { User } from '../user';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-account',
  standalone: false,
  templateUrl: './create-account.html',
  styleUrl: './create-account.css',
})
export class CreateAccount {
  private router = inject(Router);
  createdUser: User = {
    id: 0,
    userName: '',
    password: '',
    security: [],
    basket: [],
    restricted: false
  };

  public answer1: string = '';
  public answer2: string = '';

  constructor(private sessionService: SessionService, private userService: UserService) {}

  onCheckboxClick(): void {
    let isChecked = document.getElementById('create-user-show-box') as HTMLInputElement;
    let passwordInput = document.getElementById('create-user-password') as HTMLInputElement;
    if (isChecked.checked) {
      passwordInput.type = 'text';
    } else {
      passwordInput.type = 'password';
    }
  }

  onSubmit(): void {
    if (!this.createdUser.userName || !this.createdUser.password || !this.answer1 || !this.answer2) {
      document.getElementById('error-text')!.innerHTML = 'Missing required field!';
    } else {
      let tempSecurity: string[] = [this.answer1, this.answer2];
      this.createdUser.security = tempSecurity
      this.userService.addUser(this.createdUser).subscribe((newUser) => {
        if (newUser) {
          console.log("Create", newUser);
          this.router.navigate(['']);
        } else {
          document.getElementById('error-text')!.innerHTML =
            'A user with this username already exists!';
        }
      });
    }
  }

  onReturnClick(): void {
    this.router.navigate(['']);
  }
}
