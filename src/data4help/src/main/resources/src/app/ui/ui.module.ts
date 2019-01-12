import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';

import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {DashboardNavbarComponent} from './dashboard-navbar/dashboard-navbar.component';
import { ErrorUnauthorizedComponent } from './error-unauthorized/error-unauthorized.component';

@NgModule({
    declarations: [
        HeaderComponent,
        FooterComponent,
        SidebarComponent,
        DashboardNavbarComponent,
        ErrorUnauthorizedComponent
    ],
    imports: [
        CommonModule,
        RouterModule
    ],
    exports: [HeaderComponent, FooterComponent, SidebarComponent, DashboardNavbarComponent]
})
export class UiModule {
}
