import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Pages (Legal)
import { PrivacyComponent } from './pages/legal/privacy/privacy.component';
import { TermsComponent } from './pages/legal/terms/terms.component';

// Pages (User)
import { ProfileComponent } from './pages/user/profile/profile.component';
import { LoginComponent } from './pages/user/login/login.component';

const routes: Routes = [
  // Pages (Legal)
  { path: 'legal/privacy', component: PrivacyComponent },
  { path: 'legal/terms',   component: TermsComponent   },

  // Pages (User)
  { path: 'user/login',    component: LoginComponent   },
  { path: 'user/profile',  component: ProfileComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
