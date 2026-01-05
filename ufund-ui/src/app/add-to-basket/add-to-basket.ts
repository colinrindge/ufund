import { Component, Input, OnInit } from '@angular/core';
import { Need } from '../need';
import { User } from '../user';
import { UserService } from '../user-service';

@Component({
  selector: 'app-add-to-basket',
  templateUrl: './add-to-basket.html',
  styleUrls: ['./add-to-basket.css'],
  standalone: false,
})
export class AddToBasket implements OnInit {
  currentUser!: User;
  @Input() need!: Need;

  count: number = 1;

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe((data) => (this.currentUser = data));
  }

  constructor(private userService: UserService) {}

  addToBasket(): void {
    if (!this.currentUser || !this.need) {
      console.error('No user or need selected');
      return;
    }

    const numericCount = Math.max(1, Math.floor(Number(this.count)));
    if (isNaN(numericCount)) {
      console.error('Invalid count:', this.count);
      return;
    }

    this.userService.addNeed(this.currentUser, this.need).subscribe({
      next: (updatedUser) => {
        this.currentUser = updatedUser;

        this.userService.editCount(this.currentUser, this.need, numericCount).subscribe({
          next: (updatedUserWithCount) => {
            this.currentUser = updatedUserWithCount;
            console.log('Basket updated:', this.currentUser);
          },
          error: (err) => console.error('Failed to edit count:', err),
        });
      },
      error: (err) => console.error('Failed to add need:', err),
    });
  }
}
