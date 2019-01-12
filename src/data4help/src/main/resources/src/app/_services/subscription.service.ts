import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
    providedIn: 'root' // this service should be created by the root application injector.
})
export class SubscriptionService {

    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) {
    }

    createSubscription(subscription) {
        return this.http.post(`${this.baseUrl}/web/subscriptions/`, subscription);
    }

    getAllSubscriptions() {
        return this.http.get(`${this.baseUrl}/web/subscriptions/`);
    }
}
