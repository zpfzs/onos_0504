import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HustComponent } from './hust.component';

describe('HustComponent', () => {
  let component: HustComponent;
  let fixture: ComponentFixture<HustComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HustComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HustComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
