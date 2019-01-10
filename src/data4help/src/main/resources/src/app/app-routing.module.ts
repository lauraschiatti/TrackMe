import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import { Data4helpComponent } from './data4help/data4help.component';
import { AutomatedSOSComponent } from './automated-sos/automated-sos.component';
import { Track4runComponent } from './track4run/track4run.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { RequestComponent } from './request/request.component';
import { ProfileComponent } from './profile/profile.component';
import { SearchComponent } from './search/search.component';
import { AuthGuard } from './_guards/auth.guard';

import { Role } from './_models';

const routes: Routes = [
    { path : '', component : HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'data4help', component: Data4helpComponent },
    { path: 'automatedsos', component: AutomatedSOSComponent },
    { path: 'track4run', component: Track4runComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
    { path: 'search', component: SearchComponent, canActivate: [AuthGuard], data: { roles: [Role.ThirdParty]}},
    { path: 'requests', component: RequestComponent, canActivate: [AuthGuard], data: { roles: [Role.Individual]}},
    { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
    // otherwise redirect to home
    { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
