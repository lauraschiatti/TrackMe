import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import { Data4helpComponent } from './data4help/data4help.component';
import { AutomatedSOSComponent } from './automated-sos/automated-sos.component';
import {Track4runComponent} from './track4run/track4run.component';

const routes: Routes = [
  // { path: '', redirectTo: '', pathMatch: 'full' },
  { path : '', component : HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'data4help', component: Data4helpComponent },
  { path: 'automatedsos', component: AutomatedSOSComponent },
  { path: 'track4run', component: Track4runComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }


