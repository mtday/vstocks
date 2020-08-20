import { Component, OnInit } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params.token) {
        this.authService.token = params.token;

        // navigate to this same page but strip out all the query params
        const url: string = this.router.url.substring(0, this.router.url.indexOf("?"));
        this.router.navigateByUrl(url);
      }
    })
  }
}
