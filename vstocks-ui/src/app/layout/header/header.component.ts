import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  public isLoggedIn: boolean = false;

  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.tokenObservable.subscribe(token => {
      this.isLoggedIn = token != null;
    })
  }

  logout(): void {
    this.http.get<string>('/api/v1/security/logout');
    this.authService.clear();
    this.router.navigate(['/']);
  }
}
