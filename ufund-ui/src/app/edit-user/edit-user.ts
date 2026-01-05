import { Component, Output, EventEmitter, Input, OnInit, } from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../user-service';
import { User } from '../user';

@Component({
  selector: 'app-edit-user',
  standalone: false,
  templateUrl: './edit-user.html',
  styleUrl: './edit-user.css',
})
export class EditUser {
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


  constructor(private userService: UserService, private formBuilder: FormBuilder) {}

  ngOnInit() {
    if(this.currentUser != null && this.currentUser.userName != null) {
      this.user.userName = this.currentUser.userName
    }
  }

  onSubmit(): void {
    if(this.user.password == ""){
      this.closeEvent.emit();
    }
    if(this.user.userName != ""){
      this.user.id = this.currentUser.id
      this.userService.getUserByID(this.currentUser.id).subscribe((data) => {
        this.user.basket = data.basket
        this.userService.updateUser(this.user).subscribe(() => {
          this.closeEvent.emit();
          this.logOutEvent.emit();
      })
    });
    }
  }
}