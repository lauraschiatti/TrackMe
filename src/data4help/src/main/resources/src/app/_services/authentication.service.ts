import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { catchError } from 'rxjs/operators';

import { User } from '../_models';

@Injectable({ providedIn: 'root' }) // this service should be created by the root application injector.
export class AuthenticationService {

    private baseUrl = environment.baseUrl;
    private currentUserSubject: BehaviorSubject<User>;
    public currentUser: Observable<User>;

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
        this.currentUser = this.currentUserSubject.asObservable();
    }

    public get currentUserValue(): User {
        return this.currentUserSubject.value;
    }

    /******** Registration **********/
    signupIndividual(individual) {
        return this.http.post(`${this.baseUrl}/web/individual/signup`, individual);
    }

    signupThirdParty(individual) {
        return this.http.post(`${this.baseUrl}/web/thirdparty/signup`, individual);
    }


    /******** Login **********/
    login(credentials) {
        return this.http.post<User>(`${this.baseUrl}/web/login`, credentials)
            .pipe(map(user => {
                // login successful if there's a jwt token in the response
                if (user && user.accessToken) {
                    // jwt token in local storage to keep user logged in between page refreshes
                    this.setCurrentUser(user);
                }

                return user;
            })
        );
    }

    logout() {
        const currentUser = this.getCurrentUser();

        if (currentUser) {
            // remove token from server
            const user = {
                'userId' : currentUser['userId'],
                'accessToken': currentUser['accessToken']
            };
            this.http.post(`${this.baseUrl}/web/logout`, user);

            localStorage.removeItem('currentUser');
            this.currentUserSubject.next(null);
            this.router.navigate(['/']);
        }
    }

    // isValidToken() {
    //     const currentUser = this.getCurrentUser();
    //
    //     const headers = new HttpHeaders({
    //         'ACCESS-TOKEN': currentUser['userId'],
    //         'USER_ID': currentUser['accessToken']
    //     });
    //
    //     return this.http.head(`${this.baseUrl}/web/`, { headers: headers})
    //         .pipe(
    //             catchError(err => {
    //                 console.log(err);
    //                 return of(null);
    //             })
    //         );
    // }

    setCurrentUser(user): void {
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        console.log('user stored');
        this.router.navigate(['/dashboard']);
    }

    getCurrentUser() {
        const currentUser = localStorage.getItem('currentUser');
        return currentUser;
    }
}
