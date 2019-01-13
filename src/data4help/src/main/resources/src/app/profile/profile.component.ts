import {Component, OnInit} from '@angular/core';
import {AuthenticationService, UserService} from '../_services';
import {FormBuilder, FormGroup} from '@angular/forms';

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    user = '';
    role = '';
    configForm: FormGroup;

    constructor(
        private userService: UserService,
        private authenticationService: AuthenticationService,
        private formBuilder: FormBuilder,
    ) {
        this.role = this.authenticationService.currentUserValue.role;
    }

    ngOnInit() {
        this.userService
            .getCurrentUserInfo()
            .subscribe(
                data => {
                    this.user = data['data'];
                    console.log('getCurrentUserInfo', this.user);
                },
                error => {
                    console.log('error', error);
                });

        this.configForm = this.formBuilder.group({
            individualPushUrl: [''],
            bulkPushUrl: [''],
            notificationUrl: ['']
        });

    }

    get f() {
        return this.configForm.controls;
    }

    onSubmitConfig() {
        if (this.configForm.invalid) {
            return;
        }

        let individualPushUrl = this.user['config']['individualPushUrl'];
        let bulkPushUrl = this.user['config']['bulkPushUrl'];
        let notificationUrl = this.user['config']['notificationUrl'];

        if (this.f.individualPushUrl.value !== '') {
            individualPushUrl = this.f.individualPushUrl.value;
        }

        if (this.f.bulkPushUrl.value !== '') {
            bulkPushUrl = this.f.bulkPushUrl.value;
        }

        if (this.f.notificationUrl.value !== '') {
            notificationUrl = this.f.notificationUrl.value;
        }

        const config = {
            'individualPushUrl': individualPushUrl,
            'bulkPushUrl': bulkPushUrl,
            'notificationUrl': notificationUrl
        };

        this.userService
            .updateThirdPartyConfig(config)
            .subscribe(
                data => {
                    location.reload(true);
                    console.log('update config data', data['data']);
                },
                error => {
                    console.log('update config error ', error);
                }
            );
    }

}
