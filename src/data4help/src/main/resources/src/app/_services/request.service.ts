import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root' // this service should be created by the root application injector.
})
export class RequestService {

    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) { }

    getAllRequests() {
        return this.http.get(`${this.baseUrl}/web/requests/`);
    }

    updateRequestStatus(requestId) {
        return this.http.patch(`${this.baseUrl}/web/requests`, null , requestId);
    }

    createRequest() {

    }

    deleteRequest() {

    }

}
