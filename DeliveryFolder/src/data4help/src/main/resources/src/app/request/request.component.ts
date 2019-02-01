import {Component, OnInit} from '@angular/core';
import {RequestService, UserService} from '../_services';

@Component({
    selector: 'app-request',
    templateUrl: './request.component.html',
    styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit {
    user = '';
    requests = [];

    constructor(
        private userService: UserService,
        private requestService: RequestService,
    ) {
    }

    ngOnInit() {
        this.userService
            .getCurrentUserInfo()
            .subscribe(
                data => {
                    this.user = data['data'];
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
    }

    onUpdateStatus(request, selectIndex) {
        const status = (<HTMLInputElement>document.getElementById(selectIndex)).value;

        const body = {
            'ssn': this.user['ssn'],
            'status': status
        };

        const id = request['id'];

        this.requestService
            .updateRequestStatus(body, id)
            .subscribe(
                data => {
                    location.reload(true);
                    console.log('update request status', data['data']);
                },
                error => {
                    console.log('update request status error ', error);
                }
            );

    }

}
