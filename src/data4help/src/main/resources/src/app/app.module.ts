import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';

import {AppComponent} from './app.component';

import {UiModule} from './ui/ui.module';
import {LoginComponent} from './login/login.component';
import {HomeComponent} from './ui/home/home.component';
import {RegisterComponent} from './register/register.component';
import {Data4helpComponent} from './ui/data4help/data4help.component';
import {AutomatedSOSComponent} from './ui/automated-sos/automated-sos.component';
import {Track4runComponent} from './ui/track4run/track4run.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {GlobalErrorHandler, TokenInterceptor} from './_helpers';
import {RequestComponent} from './request/request.component';
import {ProfileComponent} from './profile/profile.component';
import {SearchComponent} from './search/search.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        HomeComponent,
        RegisterComponent,
        Data4helpComponent,
        AutomatedSOSComponent,
        Track4runComponent,
        DashboardComponent,
        RequestComponent,
        ProfileComponent,
        SearchComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule,
        UiModule,
        HttpClientModule
    ],
    providers: [
        {
            provide: HTTP_INTERCEPTORS,
            useClass: TokenInterceptor,
            multi: true
        },
        GlobalErrorHandler
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
