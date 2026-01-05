import { Component, inject } from '@angular/core';
import { UserService } from '../user-service';
import { SessionService } from '../session-service';
import { User } from '../user';
import { Router } from '@angular/router';
@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css'
})
export class ForgotPassword {
  private router = inject(Router);
  public userName: string = '';
  public verified: boolean = false;

  recoveredUser: User = {
    id: 0,
    userName: '',
    password: '',
    security: [],
    basket: [],
    restricted: false
  };

  public answer1: string = '';
  public answer2: string = '';

  public newPass: string = ''; 
  public verifyPass: string = '';

  constructor(private sessionService: SessionService, private userService: UserService) {}


  onSubmit(): void {
    if (!this.userName || !this.answer1 || !this.answer2) {
      document.getElementById('error-text')!.innerHTML = 'Missing required field!';
    } else {
      this.userService.getUser(this.userName).subscribe((recoveredUser) => {
        if (recoveredUser) {
          let security1 = recoveredUser.security[0];
          let security2 = recoveredUser.security[1];
          if (security1 == this.answer1 && security2 == this.answer2) {
            this.recoveredUser = recoveredUser;
            this.verified = true;
          } else {
            document.getElementById('error-text')!.innerHTML =
              'Username or Security Questions incorrect!';
          }
        } else {
          console.log(recoveredUser);
          console.log(this.userName);
          document.getElementById('error-text')!.innerHTML =
            'Username or Security Questions incorrect!';
        }
      });
    }
  }

  onRecover(): void {
    if (this.newPass || this.verifyPass) {
      if (this.newPass == this.verifyPass) {
        
      this.sessionService.loginHash(this.recoveredUser.userName, this.recoveredUser.password).subscribe({
        next: (session) => {
          if (session) {            
            this.recoveredUser.password = this.newPass;
            this.userService.updateUser(this.recoveredUser).subscribe((updatedUser) => {
              if (updatedUser) {
                try {
                      this.sessionService.deleteSession(this.recoveredUser.userName).subscribe(() => {
                      this.router.navigate(['/']);
                      });
                    } catch (error) {
                      return;
                    }


                this.router.navigate(['']);
              } else {
                
                document.getElementById('error-text')!.innerHTML =
                  'Error updating password. Please try again.';
              }
            });

          } else {
            document.getElementById('error-text')!.innerHTML = 'Error logging in. Please try again.';
          }
        },
      });

      } else {
        document.getElementById('error-text')!.innerHTML = 'Passwords do not match!';
      }
    } else {
      document.getElementById('error-text')!.innerHTML = 'Missing required field!';
    }
  }

  onReturnClick(): void {
    this.router.navigate(['']);
  }
}
