import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { isNullOrUndefined } from 'util';

// we declare that this service should be created
// by the root application injector.
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

    baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) {}
    headers: HttpHeaders = new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
    });

    signupIndividual(individual) {
        return this.http.post(`${this.baseUrl}/web/individual/signup`, individual, {headers: this.headers});
    }

    // signupThirdParty(individual) {
    //     return this.http.post(`${this.baseUrl}/web/thirdparty/signup`, thirdParty);
    // }

    // setCurrentUser(userId, accessToken): void {
    //     const user = { 'userId': userId, 'accessToken': accessToken };
    //     localStorage.setItem('currentUser', JSON.stringify(user));
    // }

    // getCurrentUser() {
    //     let user = localStorage.getItem('currentUser');
    //     if (!isNullOrUndefined(user)) {
    //         user = JSON.parse(user);
    //         return user;
    //     } else {
    //         return null;
    //     }
    // }

    //
    // login(username: string, password: string) {
    //     return this.http.post<any>(`${config.apiUrl}/users/authenticate`, { username, password })
    //         .pipe(map(user => {
    //             // login successful if there's a jwt token in the response
    //             if (user && user.token) {
    //                 // store user details and jwt token in local storage to keep user logged in between page refreshes
    //                 localStorage.setItem('currentUser', JSON.stringify(user));
    //                 this.currentUserSubject.next(user);
    //             }
    //
    //             return user;
    //         }));
    // }
    //
    // logout() {
    //     // remove user from local storage to log user out
    //     localStorage.removeItem('currentUser');
    //     this.currentUserSubject.next(null);
    // }
}
