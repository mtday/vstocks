import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

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
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
