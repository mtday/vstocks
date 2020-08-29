import { Component, OnInit } from '@angular/core';

import { PortfolioService } from 'src/app/services/portfolio.service';
import { PortfolioValue } from 'src/app/models/models';

@Component({
  selector: 'app-portfolio-value',
  templateUrl: './portfolio-value.component.html',
  styleUrls: ['./portfolio-value.component.scss']
})
export class PortfolioValueComponent implements OnInit {
  public portfolioValue: PortfolioValue;

  constructor(private portfolioService: PortfolioService) { }

  ngOnInit(): void {
    this.portfolioService.getPortfolioValue().subscribe(portfolioValue => this.portfolioValue = portfolioValue);
  }
}
