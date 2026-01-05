import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CupboardList } from './cupboard-list';

describe('CupboardList', () => {
  let component: CupboardList;
  let fixture: ComponentFixture<CupboardList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CupboardList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CupboardList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
