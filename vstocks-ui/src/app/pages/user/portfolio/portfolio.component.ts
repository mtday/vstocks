import { Component, OnInit } from '@angular/core';

import { PortfolioService } from '../../../services/portfolio.service';
import {
  ActivityLog,
  CreditRankCollection,
  MarketRankCollection,
  MarketTotalRankCollection,
  PricedUserStock,
  Results,
  TotalRankCollection,
} from 'src/app/models/models';

@Component({
  selector: 'app-user-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.scss']
})
export class UserPortfolioComponent implements OnInit {
  public pricedUserStocks: Results<PricedUserStock>;
  public marketActivity: Results<ActivityLog>;

  public creditRanks: CreditRankCollection;
  public marketRanks: MarketRankCollection[];
  public marketTotalRanks: MarketTotalRankCollection;
  public totalRanks: TotalRankCollection;

  constructor(private portfolioService: PortfolioService) { }

  ngOnInit(): void {
    this.portfolioService.getStocks(null, null).subscribe(pricedUserStocks => this.pricedUserStocks = pricedUserStocks);
    this.portfolioService.getAllMarketActivity(null, null).subscribe(marketActivity => this.marketActivity = marketActivity);

    this.portfolioService.getCreditRank().subscribe(creditRanks => this.creditRanks = creditRanks);
    this.portfolioService.getMarketRanks().subscribe(marketRanks => this.marketRanks = marketRanks);
    this.portfolioService.getMarketTotalRank().subscribe(marketTotalRanks => this.marketTotalRanks = marketTotalRanks);
    this.portfolioService.getTotalRank().subscribe(totalRanks => this.totalRanks = totalRanks);
  }

}
