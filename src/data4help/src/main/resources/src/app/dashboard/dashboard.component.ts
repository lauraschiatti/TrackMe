import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

import { UserService, AuthenticationService, SubscriptionService, SearchService, RequestService } from '../_services';
import { BloodType, Gender } from '../_models';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
    user = '';
    subscriptions = '';
    role = '';
    request = '';

    iForm: FormGroup;
    bulkForm: FormGroup;
    iFormSubmitted = false;
    error = '';
    errorBulk =  '';

    bloodTypes = BloodType.values();
    Gender = Gender.values();

    noSubscriptions = false;
    showSendRequest = false;
    showRequestInfo = false;


    constructor(
        private authenticationService: AuthenticationService,
        private userService: UserService,
        private subscriptionService: SubscriptionService,
        private searchService: SearchService,
        private requestService: RequestService,
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
                    console.log('data getAllSubscriptions', data);
                    if (data['data'].length > 0) {
                        this.subscriptions = data['data'];
                        console.log('requests', this.subscriptions);
                    } else {
                        this.noSubscriptions = true;
                        // console.log ('noSubscriptions', this.noSubscriptions);
                    }
                },
                error => {
                    console.log('error getAllSubscriptions', error);
                });


        // Validate search forms
        this.iForm = this.iFormBuilder.group({
            ssn: ['', Validators.required],
        });


        this.bulkForm = this.bulkFormBuilder.group({
            gender: [''],
            bloodType: [''],
            minAge: [''],
            maxAge: [''],
            city: [''],
            province: [''],
            country: [''],

            subscription: [''],
            timeSpan: ['']
        });

    }

    get iControls() { return this.iForm.controls; }
    get bulkControls() { return this.bulkForm.controls; }

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

                        if (error === 'Should send a request to the individual to access his data') {
                            this.showSendRequest = true;
                        }

                        console.log('individual search error ', error);
                    }
                );
        }
    }

    onSubmitBulkSearch() {
        this.searchService
            .search(null, this.bulkForm.value)
            .subscribe(
                data => {
                    // display data
                    console.log('search: bulk data', data);

                },
                error => {
                    this.errorBulk = error;
                    console.log('error ', error);
                }
            );

        // Create subscription to data
        if (this.bulkControls.subscription.value) {
            let timeSpan = 6;
            if (this.bulkControls.timeSpan.value) {
                timeSpan = this.bulkControls.timeSpan.value;
            }

            const subscription = {
                'filter': {
                    'gender' : this.bulkControls.gender.value,
                    'bloodType': this.bulkControls.bloodType.value,
                    'minAge': Number(this.bulkControls.minAge.value),
                    'maxAge': Number(this.bulkControls.maxAge.value),
                    'city' : this.bulkControls.city.value,
                    'province': this.bulkControls.province.value,
                    'country' : this.bulkControls.country.value
                },
                'timeSpan': timeSpan
            };

            console.log('subscription', subscription);

            this.subscriptionService
                .createSubscription(subscription)
                .subscribe(
                    data => {
                        // display data
                        console.log('subscription data', data);

                    },
                    error => {
                        console.log('subscription error ', error);
                    }
                );
        }
    }

    onSendRequest() {
        const ssn = {
            'ssn' : this.iControls.ssn.value
        };

        this.requestService
            .createRequest(ssn)
            .subscribe(
                data => {
                    this.request = data['data'];
                    this.showSendRequest = false;
                    this.showRequestInfo = true;
                    console.log('create request data', data);
                },
                error => {
                    console.log('create request error', error);
                });
    }
}
