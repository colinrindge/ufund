import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasketOverlay } from './basket-component';

describe('CupboardList', () => {
  let component: BasketOverlay;
  let fixture: ComponentFixture<BasketOverlay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BasketOverlay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BasketOverlay);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
