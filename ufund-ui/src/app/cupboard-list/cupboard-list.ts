import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../cupboard-service';
import { User } from '../user';
import { SessionService } from '../session-service';
import { UserService } from '../user-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cupboard-list',
  standalone: false,
  templateUrl: './cupboard-list.html',
  styleUrl: './cupboard-list.css',
})
export class CupboardList implements OnInit, OnDestroy {
  @Input() needs!: Need[];
  @Input() currentUser!: User;
  private router = inject(Router);

  constructor(
    private NeedService: NeedService,
    private userService: UserService,
    private sessionService: SessionService
  ) {}
  searchTerm: string = '';

  ngOnInit(): void {
    this.NeedService.getCurrentNeeds().subscribe((data) => (this.needs = data));
    this.userService.getCurrentUser().subscribe((data) => (this.currentUser = data));
    this.getNeeds();
  }

  ngOnDestroy(): void {
    this.NeedService.getCurrentNeeds().subscribe((data) => (data.length = 0));
  }

  getNeeds(): void {
    this.NeedService.getNeeds().subscribe((storedneeds) =>
      storedneeds.forEach((element) => {
        this.needs.push(element);
      })
    );
  }
  isAdmin(): boolean {
    return this.currentUser?.userName === 'admin';
  }

  getNeedbyName(): void {
    this.needs.length = 0;
    if (this.searchTerm == '') {
      this.getNeeds();
      return;
    }
    this.NeedService.getNeedbyName(this.searchTerm).subscribe((storedneeds) => {
      storedneeds.forEach((element) => {
        this.needs.push(element);
      });
    });
  }

  goToCreate(): void {
    this.router.navigate(['']);
  }
}
