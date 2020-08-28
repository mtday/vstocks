import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

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

const routes: Routes = [
  // Pages (Public)
  { path: '',                  component: PublicHomeComponent       },
  { path: 'public/dashboard',  component: PublicDashboardComponent  },
  { path: 'public/home',       component: PublicHomeComponent       },
  { path: 'public/standings',  component: PublicStandingsComponent  },

  // Pages (Legal)
  { path: 'legal/privacy',     component: LegalPrivacyComponent     },
  { path: 'legal/terms',       component: LegalTermsComponent       },

  // Pages (User)
  { path: 'user/achievements', component: UserAchievementsComponent },
  { path: 'user/dashboard',    component: UserDashboardComponent    },
  { path: 'user/login',        component: UserLoginComponent        },
  { path: 'user/portfolio',    component: UserPortfolioComponent    },
  { path: 'user/profile',      component: UserProfileComponent      },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
