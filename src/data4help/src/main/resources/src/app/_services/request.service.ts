import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';
import {map} from 'rxjs/operators';

import { User } from '../_models';

@Injectable({
    providedIn: 'root' // this service should be created by the root application injector.
})
export class AuthenticationService {

    private baseUrl = environment.baseUrl;
    // private currentUserSubject: BehaviorSubject<User>;
    // public currentUser: Observable<User>;

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        // this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
        // this.currentUser = this.currentUserSubject.asObservable();
    }

    signupIndividual(individual) {
        return this.http.get(`${this.baseUrl}/web/individual/signup`, individual);
    }

}
