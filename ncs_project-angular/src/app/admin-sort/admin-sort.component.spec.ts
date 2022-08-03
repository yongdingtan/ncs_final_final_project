import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminSortComponent } from './admin-sort.component';

describe('AdminSortComponent', () => {
  let component: AdminSortComponent;
  let fixture: ComponentFixture<AdminSortComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminSortComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminSortComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
