import { Component, OnInit } from '@angular/core';

import { PortfolioService } from 'src/app/services/portfolio.service';
import { Results, StockActivityLog } from 'src/app/models/models';

@Component({
  selector: 'app-portfolio-activity',
  templateUrl: './portfolio-activity.component.html',
  styleUrls: ['./portfolio-activity.component.scss']
})
export class PortfolioActivityComponent implements OnInit {
  public portfolioActivity: Results<StockActivityLog>;

  constructor(private portfolioService: PortfolioService) { }

  ngOnInit(): void {
    this.portfolioService.getAllMarketActivity(null, null).subscribe(
      portfolioActivity => this.portfolioActivity = portfolioActivity);
  }
}
