import { Component, Output, EventEmitter, Input, ViewChild, ElementRef, OnInit} from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Need } from '../need';
import { User } from '../user';
import { UserService } from '../user-service';
import { NeedService } from '../cupboard-service';
import { BasketNeed } from '../basketNeed';

@Component({
  selector: 'app-basket-component',
  standalone: false,
  templateUrl: './basket-component.html',
  styleUrl: './basket-component.css',
})
export class BasketOverlay implements OnInit {
  @ViewChild('basketDialog') myDialog!: ElementRef<HTMLDialogElement>;
  @Output() closeEvent = new EventEmitter<void>();
  @Input() basket!: BasketNeed[];
  user!: User;
  needs!: Need[];

  checkOutValidated = false;

  skippedBasket: BasketNeed[] = [];

  constructor(
    private userService: UserService,
    private needService: NeedService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.needService.getCurrentNeeds().subscribe((data) => (this.needs = data));
    this.userService.getCurrentUser().subscribe((data) => (this.user = data));
  }

  closeBasket(): void {
    this.closeEvent.emit();
  }

  removeNeed(basketNeed: BasketNeed): void {
    this.basket.splice(this.basket.indexOf(basketNeed), 1);
    this.basket = [...this.basket];
    this.userService.removeNeed(this.user.id, basketNeed.need).subscribe();
  }

  updateBasket(): BasketNeed[] {
    for (var basketNeed of this.basket) {
      this.updateNeed(basketNeed);
    }
    return this.basket;
  }

  updateNeed(basketNeed: BasketNeed, newCount: number = 0): boolean {
    for (var need of this.needs) {
      if (need.id === basketNeed.need.id) {
        basketNeed.need = need;
        if (newCount != 0) {
          if (newCount < 0) {
            return false;
          }
          if (newCount >= basketNeed.count && newCount > basketNeed.need.cost - basketNeed.need.quantity) {
            newCount = basketNeed.need.cost - basketNeed.need.quantity;
          }
          basketNeed.count = newCount;
          this.userService.editCount(this.user, basketNeed.need, basketNeed.count).subscribe({
            next: (updatedUserWithCount) => {
              this.user = updatedUserWithCount;
            },
            error: (err) => console.error('Failed to edit count:', err),
          });
        }
        return true;
      }
    }
    this.removeNeed(basketNeed);
    return false;
  }

  checkOutBasket(): void {
    // for each element,
    //      if quantity + count doesn't exceed cost then update need
    //      if it does exceed cost, add to skippedBasket
    for (var basketNeed of this.basket) {
      var need = basketNeed.need;
      var count = basketNeed.count;

      var totalCount = need.quantity + count;

      if (totalCount > need.cost) {
        this.skippedBasket.push(basketNeed);
        continue;
      } else {
        need.quantity = totalCount;
        this.needs.find((cupboardneed) => cupboardneed.id === need.id)!.quantity = totalCount;
        this.needs = [...this.needs];
        this.needService.updateNeed(need).subscribe();
        this.userService.removeNeed(this.user.id, need).subscribe();
      }
    }

    // once done, set basket to all skipped needs and empty skippedBasket
    this.basket = this.skippedBasket;
    this.skippedBasket = [];

    // and update user's basket
    if (this.basket.length != 0) {
      this.checkOutValidated = true;
    }

    return;
  }
}
