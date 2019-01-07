import { Component, OnInit } from '@angular/core';
import { UserService, AuthenticationService, SubscriptionService } from '../_services';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  private user;
  private subscriptions;
  private role;
  private noSubscriptions = false;

  constructor(
      private authenticationService: AuthenticationService,
      private userService: UserService,
      private subscriptionService: SubscriptionService,
  ) {
      this.role = this.authenticationService.currentUserValue.role;
  }

  ngOnInit() {
      this.userService
          .getCurrentUserInfo()
          .subscribe(
              data => {
                  this.user = data['data'];
                  console.log('user', this.role);
              },
              error => {
                  console.log('error', error);
              });

      this.subscriptionService
          .getAllSubscriptions()
          .subscribe(
              data => {
                  if (data['data'].length > 0) {
                      this.subscriptions = data['data'];
                      console.log('requests', this.subscriptions);
                  } else {
                      this.noSubscriptions = true;
                      console.log ('noRequests', this.noSubscriptions);
                  }
              },
              error => {
                  console.log('error', error);
              });
  }

}
