import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioActivityComponent } from './portfolio-activity.component';

describe('PortfolioActivityComponent', () => {
  let component: PortfolioActivityComponent;
  let fixture: ComponentFixture<PortfolioActivityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PortfolioActivityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PortfolioActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
