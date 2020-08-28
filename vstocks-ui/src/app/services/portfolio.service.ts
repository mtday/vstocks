import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  ActivityLog,
  CreditRankCollection,
  MarketRankCollection,
  MarketTotalRankCollection,
  Page,
  PricedUserStock,
  Results,
  Sort,
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

  getStocks(page: Page, sort: Sort[]): Observable<Results<PricedUserStock>> {
    const params: HttpParams = new HttpParams();
    if (page) {
      params.append("pageNum", page.page.toString());
      params.append("pageSize", page.size.toString());
    }
    return this.http.get<Results<PricedUserStock>>('/api/user/portfolio/stocks', { params: params });
  }

  getAllMarketActivity(page: Page, sort: Sort[]): Observable<Results<ActivityLog>> {
    const params: HttpParams = new HttpParams();
    if (page) {
      params.append("pageNum", page.page.toString());
      params.append("pageSize", page.size.toString());
    }
    return this.http.get<Results<ActivityLog>>('/api/user/portfolio/market/activity', { params: params });
  }

  getMarketActivity(market: string, page: Page, sort: Sort[]): Observable<Results<ActivityLog>> {
    const params: HttpParams = new HttpParams();
    if (page) {
      params.append("pageNum", page.page.toString());
      params.append("pageSize", page.size.toString());
    }
    return this.http.get<Results<ActivityLog>>('/api/user/portfolio/market/' + market + '/activity', { params: params });
  }

  // ranks

  getCreditRank(): Observable<CreditRankCollection> {
    return this.http.get<CreditRankCollection>('/api/user/portfolio/rank/credits');
  }

  getMarketRanks(): Observable<MarketRankCollection[]> {
    return this.http.get<MarketRankCollection[]>('/api/user/portfolio/rank/markets/');
  }

  getMarketRank(market: string): Observable<MarketRankCollection> {
    return this.http.get<MarketRankCollection>('/api/user/portfolio/rank/market/' + market);
  }

  getMarketTotalRank(): Observable<MarketTotalRankCollection> {
    return this.http.get<MarketTotalRankCollection>('/api/user/portfolio/rank/market-total');
  }

  getTotalRank(): Observable<TotalRankCollection> {
    return this.http.get<TotalRankCollection>('/api/user/portfolio/rank/total');
  }
}
