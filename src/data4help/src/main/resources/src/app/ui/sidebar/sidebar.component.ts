import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from '../../_services';

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

    private role;

    constructor(
        private authenticationService: AuthenticationService
    ) {
        this.role = this.authenticationService.currentUserValue.role;
    }

    ngOnInit() {
    }

    logout() {
        this.authenticationService.logout();
    }
}
