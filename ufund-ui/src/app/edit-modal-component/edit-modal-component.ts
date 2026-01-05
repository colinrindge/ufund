import { Component, Output, EventEmitter, Input } from '@angular/core';
import { Form, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { NeedService } from '../cupboard-service';
import { Need } from '../need';

@Component({
  selector: 'app-edit-modal-component',
  standalone: false,
  templateUrl: './edit-modal-component.html',
  styleUrl: './edit-modal-component.css',
})
export class EditModalComponent {
  @Output() closeEvent = new EventEmitter<void>();
  @Input() need!: Need;

  constructor(private needService: NeedService, private formBuilder: FormBuilder) {}

  onSubmit(): void {
    this.needService.updateNeed(this.need).subscribe(() => {
      this.closeEvent.emit();
    });
  }
}
