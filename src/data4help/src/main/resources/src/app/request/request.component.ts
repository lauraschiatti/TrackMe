import { Component, OnInit } from '@angular/core';
import { RequestService } from '../_services';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-request',
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit {

    requests = [];
    updateStatusForm: FormGroup;
    // updateStatusFormSubmitted = false;

    constructor(
        private requestService: RequestService,
        private updateStatusFormBuilder: FormBuilder,
    ) {
    }

    ngOnInit() {
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
