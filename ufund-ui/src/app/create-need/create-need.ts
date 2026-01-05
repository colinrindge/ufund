import { Component, Input } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../cupboard-service';

@Component({
  selector: 'app-create-need',
  standalone: false,
  templateUrl: './create-need.html',
  styleUrl: './create-need.css',
})
export class CreateNeed {
  @Input() needs!: Need[];
  need: Need = {
    id: 0,
    name: '',
    cost: 0,
    quantity: 0,
    type: '',
    description: '',
  };
  isCreating = false;

  openCreateMenu() {
    this.isCreating = true;
  }

  closeCreateMenu() {
    this.isCreating = false;
    this.need = {
      id: 0,
      name: '',
      cost: 0,
      quantity: 0,
      type: '',
      description: '',
    };
  }

  constructor(private NeedService: NeedService) {}
}
