import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Interceptors
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { UnauthorizedInterceptor } from './interceptors/unauthorized.interceptor';

// Layouts
import { HeaderComponent } from './layout/header/header.component';
import { FooterComponent } from './layout/footer/footer.component';

// Pages (Public)
import { PublicDashboardComponent } from './pages/public/dashboard/dashboard.component';
import { PublicHomeComponent      } from './pages/public/home/home.component';
import { PublicStandingsComponent } from './pages/public/standings/standings.component';

// Pages (Legal)
import { LegalPrivacyComponent } from './pages/legal/privacy/privacy.component';
import { LegalTermsComponent   } from './pages/legal/terms/terms.component';

// Pages (User)
import { UserAchievementsComponent } from './pages/user/achievements/achievements.component';
import { UserDashboardComponent    } from './pages/user/dashboard/dashboard.component';
import { UserLoginComponent        } from './pages/user/login/login.component';
import { UserPortfolioComponent    } from './pages/user/portfolio/portfolio.component';
import { UserProfileComponent      } from './pages/user/profile/profile.component';

@NgModule({
  declarations: [
    AppComponent,

    // Layout
    HeaderComponent,
    FooterComponent,

    // pages (Public)
    PublicDashboardComponent,
    PublicHomeComponent,
    PublicStandingsComponent,

    // Pages (Legal)
    LegalPrivacyComponent,
    LegalTermsComponent,

    // Pages (User)
    UserAchievementsComponent,
    UserDashboardComponent,
    UserLoginComponent,
    UserPortfolioComponent,
    UserProfileComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
