import {Component, OnInit} from '@angular/core';
import {User} from '../../_models';
import {AuthenticationService} from '../../_services';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
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

