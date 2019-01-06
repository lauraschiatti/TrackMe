import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { User } from '../_models';


@Injectable({
  providedIn: 'root' // this service should be created by the root application injector.
})
export class AuthenticationService {

    baseUrl = environment.baseUrl;
    private currentUserSubject: BehaviorSubject<User>;
    public currentUser: Observable<User>;

    constructor(private http: HttpClient) {
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

    /******** Login **********/
    login(credentials) {
        return this.http.post<User>(`${this.baseUrl}/web/login`, credentials)
            .pipe(map(user => {
            // login successful if there's a jwt token in the response
            if (user && user.accessToken) {
                // jwt token in local storage to keep user logged in between page refreshes
                this.setCurrentUser(user);
                this.currentUserSubject.next(user);
            }

            console.log('login', user);
            return user;
        }));
    }

    logout() {
        const currentUser = this.getCurrentUser();

        if (currentUser) {
            // remove token from server
            const user = {
                'userId' : currentUser['userId'],
                'accessToken': currentUser['accessToken']
            };
            this.http.post<any>(`${this.baseUrl}/web/logout`, user);

            // remove user from local storage to log user out
            localStorage.removeItem('currentUser');
            this.currentUserSubject.next(null);
        }
    }

    setCurrentUser(user): void {
        localStorage.setItem('currentUser', JSON.stringify(user));
        console.log('user stored');
    }

    getCurrentUser() {
        const currentUser = localStorage.getItem('currentUser');
        return currentUser;
    }
}
