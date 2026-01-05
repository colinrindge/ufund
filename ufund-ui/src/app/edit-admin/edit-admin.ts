import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../user-service';
import { User } from '../user';

@Component({
  selector: 'app-edit-admin',
  standalone: false,
  templateUrl: './edit-admin.html',
  styleUrl: './edit-admin.css',
})
export class EditAdmin implements OnInit {
  @Output() closeEvent = new EventEmitter<void>();
  @Output() logOutEvent = new EventEmitter<void>();
  @Input() currentUser!: User;
  user: User = {
    id: 0,
    userName: '',
    basket: [],
    password: '',
    restricted: false,
    security: []
  };
  users: User[] = [];

  constructor(private userService: UserService, private formBuilder: FormBuilder) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe((usersBackend) => {
      for (let user of usersBackend) {
        if (user.userName != 'admin') {
          this.users.push(user);
        }
      }
    });
  }

  onSubmit(checkedUser: string): void {
    if (checkedUser == '' || checkedUser == 'admin') {
      console.log('invalid request');
    } else {
      this.userService.getUser(checkedUser).subscribe((data) => {
        this.user.id = data.id;
        this.user.userName = checkedUser;
        this.user.basket = data.basket;
        this.user.restricted = !data.restricted;
        this.userService.updateUser(this.user).subscribe();
      });
    }
  }

  closeRestrict(): void {
    this.closeEvent.emit();
  }
}
