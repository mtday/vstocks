import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { User, UserReset, UsernameCheck } from '../models/models';


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
}
