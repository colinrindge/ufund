import { Component, Input, OnInit } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../cupboard-service';

@Component({
  selector: 'app-admin-overlay',
  standalone: false,
  templateUrl: './admin-overlay-component.html',
  styleUrl: './admin-overlay-component.css',
})
export class Overlay {
  @Input() need!: Need;
  @Input() needs!: Need[];
  isEditing = false;

  constructor(private needService: NeedService) {}

  openEditNeed() {
    this.isEditing = true;
  }

  closeEditNeed() {
    this.isEditing = false;
  }

  deleteNeed() {
    this.needs.splice(this.needs.indexOf(this.need), 1);
    this.needs = [...this.needs];
    this.needService.deleteNeed(this.need.id).subscribe();
  }
}
