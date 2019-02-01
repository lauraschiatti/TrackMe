import {Component, OnInit} from '@angular/core';
import {User} from '../../_models';
import {AuthenticationService} from '../../_services';

@Component({
    selector: 'app-dashboard-navbar',
    templateUrl: './dashboard-navbar.component.html',
    styleUrls: ['./dashboard-navbar.component.css']
})
export class DashboardNavbarComponent implements OnInit {
    currentUser: User;

    constructor(
        private authenticationService: AuthenticationService
    ) {
        this.authenticationService.currentUser
            .subscribe(value => this.currentUser = value);
    }

    ngOnInit() {
    }

    logout() {
        this.authenticationService.logout();
    }

}
