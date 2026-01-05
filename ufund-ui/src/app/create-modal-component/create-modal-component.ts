import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../cupboard-service';

@Component({
  selector: 'app-create-modal-component',
  standalone: false,
  templateUrl: './create-modal-component.html',
  styleUrl: './create-modal-component.css',
})
export class CreateModalComponent {
  @Output() closeEvent = new EventEmitter<void>();
  @Input() need!: Need;
  @Input() needs!: Need[];

  constructor(private needService: NeedService) {}

  onSubmit(): void {
    if (this.need.name != '') {
      this.needService.addNeed(this.need).subscribe((data) => {
        this.need.id = data.id;
        this.needs.push(structuredClone(this.need));
      });
    }
    this.closeEvent.emit();
  }
}
