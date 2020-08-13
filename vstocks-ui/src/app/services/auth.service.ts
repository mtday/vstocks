import { Injectable } from '@angular/core';

import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenSubject: BehaviorSubject<string>;
  public tokenObservable: Observable<string>;

  constructor(private router: Router) {
    this.tokenSubject = new BehaviorSubject<string>(localStorage.getItem('auth:token'));
    this.tokenObservable = this.tokenSubject.asObservable();
  }

  public get token(): string {
    return this.tokenSubject.value;
  }

  public set token(tokenValue: string) {
    localStorage.setItem('auth:token', tokenValue);
    this.tokenSubject.next(tokenValue);
  }

  public clear() {
    localStorage.removeItem('auth:token');
    this.tokenSubject.next(null);
  }
}
