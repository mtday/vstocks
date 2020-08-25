import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CreditRankCollection,
  MarketRankCollection,
  MarketTotalRankCollection,
  TotalRankCollection,
  User,
  UserCredits,
  UserReset,
  UsernameCheck
} from '../models/models';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {}

  getUser(): Observable<User> {
    return this.http.get<User>('/api/user');
  }

  putUser(username: string, displayName: string): Observable<User> {
    let user: User = new User();
    user.username = username;
    user.displayName = displayName;
    return this.http.put<User>('/api/user', user);
  }

  resetUser(): Observable<UserReset> {
    return this.http.put<UserReset>('/api/user/reset', null);
  }

  checkUsername(username: string): Observable<UsernameCheck> {
    const options = { params: new HttpParams().append("username", username) };
    return this.http.get<UsernameCheck>('/api/user/check', options);
  }

  // portfolio

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
