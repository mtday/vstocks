import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioValueComponent } from './portfolio-value.component';

describe('PortfolioValueComponent', () => {
  let component: PortfolioValueComponent;
  let fixture: ComponentFixture<PortfolioValueComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PortfolioValueComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PortfolioValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
