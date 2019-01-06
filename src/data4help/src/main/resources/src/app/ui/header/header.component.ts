import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthenticationService } from '../../_services';
import { User } from '../../_models';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    currentUser: User;

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService
    ) {
        if (this.authenticationService.currentUserValue) {
            this.currentUser = this.authenticationService.currentUserValue;
        }
    }

    ngOnInit() {
    }

    logout() {
        this.authenticationService.logout();
        this.currentUser = null;
        this.router.navigate(['/login']);
    }

}
