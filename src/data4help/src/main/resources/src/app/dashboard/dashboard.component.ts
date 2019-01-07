import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

import {UserService, AuthenticationService, SubscriptionService, SearchService } from '../_services';

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

  iForm: FormGroup;
  iFormSubmitted = false;
  error = '';

  constructor(
      private authenticationService: AuthenticationService,
      private userService: UserService,
      private subscriptionService: SubscriptionService,
      private searchService: SearchService,
      private iFormBuilder: FormBuilder,
      private bulkFormBuilder: FormBuilder
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
                  if (data['data'].length > 0) {
                      this.subscriptions = data['data'];
                      console.log('requests', this.subscriptions);
                  } else {
                      this.noSubscriptions = true;
                      // console.log ('noSubscriptions', this.noSubscriptions);
                  }
              },
              error => {
                  console.log('error', error);
              });

      // Validate search forms
      this.iForm = this.iFormBuilder.group({
          ssn: ['', Validators.required],
      });
  }

  get iControls() { return this.iForm.controls; }

  onSubmitIndividualSearch() {
      this.iFormSubmitted = true;

      // stop here if form is invalid
      if (this.iForm.invalid) {
          return;
      }

      if (this.iForm.valid) {
        this.searchService
            .search(this.iControls.ssn.value)
            .subscribe(
                data => {
                    // display data
                    console.log('search: individual data', data);

                },
                error => {
                    this.error = error;
                    console.log('error ', error);
                }
            );
      }
  }


}
