import { Component, OnInit } from '@angular/core';
import { UserService, AuthenticationService, SubscriptionService } from '../_services';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
    user = '';
    subscriptions = '';
    role = '';
    error = '';

    constructor(
        private authenticationService: AuthenticationService,
        private userService: UserService,
        private subscriptionService: SubscriptionService
    ) {
        this.role = this.authenticationService.currentUserValue.role;
    }

    ngOnInit() {
        this.userService
            .getCurrentUserInfo()
            .subscribe(
                data => {
                    this.user = data['data'];
                    // console.log('user', this.role);
                },
                error => {
                    console.log('error', error);
                });

        this.subscriptionService
            .getAllSubscriptions()
            .subscribe(
                data => {
                        this.subscriptions = data['data'];
                        console.log('subscriptions', this.subscriptions);
                    },
                    error => {
                        console.log('get all subscriptions error', error);
                    });

    }
}
