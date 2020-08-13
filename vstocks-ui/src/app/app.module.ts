import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Interceptors
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { UnauthorizedInterceptor } from './interceptors/unauthorized.interceptor';

// Layouts
import { HeaderComponent } from './layout/header/header.component';
import { FooterComponent } from './layout/footer/footer.component';

// Pages (Legal)
import { PrivacyComponent } from './pages/legal/privacy/privacy.component';
import { TermsComponent } from './pages/legal/terms/terms.component';
// Pages (User)
import { LoginComponent } from './pages/user/login/login.component';
import { ProfileComponent } from './pages/user/profile/profile.component';

@NgModule({
  declarations: [
    AppComponent,

    // Layout
    HeaderComponent,
    FooterComponent,

    // Pages (Legal)
    PrivacyComponent,
    TermsComponent,

    // Pages (User)
    LoginComponent,
    ProfileComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    HttpClientModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
