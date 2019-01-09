import { Component, OnInit } from '@angular/core';
import { RequestService, UserService } from '../_services';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit {

    user = '';
    requests = [];
    updateStatusForm: FormGroup;
    // updateStatusFormSubmitted = false;

    constructor(
        private userService: UserService,
        private requestService: RequestService,
        private updateStatusFormBuilder: FormBuilder,
    ) {
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

        this.requestService
            .getAllRequests()
            .subscribe(
                data => {
                    this.requests = data['data'];
                    console.log('requests', this.requests);
                },
                error => {
                    console.log('get all requests error', error);
                });


        this.updateStatusForm = this.updateStatusFormBuilder.group({
            status: [''],
        });
    }

    get updateStatusControls() {
        return this.updateStatusForm.controls;
    }

    onSubmitUpdateStatus() {
        console.log('onSubmitUpdateStatus', this.updateStatusControls.status.value);
        // this.updateStatusFormSubmitted = true;

        // this.searchService
        //     .search(this.updateStatusControls.ssn.value)
        //     .subscribe(
        //         data => {
        //             // display data
        //             console.log('search: individual data', data);
        //
        //         },
        //         error => {
        //             this.error = error;
        //
        //             if (error === 'Should send a request to the individual to access his data') {
        //                 this.showSendRequest = true;
        //             }
        //
        //             console.log('individual search error ', error);
        //         }
        //     );

    }

}
