import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CreditRankCollection,
  MarketRankCollection,
  MarketTotalRankCollection,
  TotalRankCollection,
  UserCredits,
} from '../models/models';


@Injectable({
  providedIn: 'root'
})
export class PortfolioService {

  constructor(private http: HttpClient) {}

  getCreditBalance(): Observable<UserCredits> {
    return this.http.get<UserCredits>('/api/user/portfolio/credits');
  }

  getCreditRank(): Observable<CreditRankCollection> {
    return this.http.get<CreditRankCollection>('/api/user/portfolio/rank/credits');
  }

  getMarketRank(market: string): Observable<MarketRankCollection> {
    return this.http.get<MarketRankCollection>('/api/user/portfolio/rank/credits/' + market);
  }

  getMarketTotalRank(): Observable<MarketTotalRankCollection> {
    return this.http.get<MarketTotalRankCollection>('/api/user/portfolio/rank/market-total');
  }

  getTotalRank(): Observable<TotalRankCollection> {
    return this.http.get<TotalRankCollection>('/api/user/portfolio/rank/total');
  }
}
