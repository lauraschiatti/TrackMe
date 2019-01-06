import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

import { AuthenticationService } from '../_services';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    constructor(
        private authenticationService: AuthenticationService,
        private router: Router,
    ) {}

    // include userId accessToken (in localstorage) as headers in any HTTP request that is sent
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        const returnUrl = this.router.url;

        // add header for all requests except login and register
        if (returnUrl !== '/login' && returnUrl !== '/register') {
            // const currentUser = this.authenticationService.getCurrentUser();
            const currentUser = this.authenticationService.currentUserValue;

            if (currentUser) {
                request = request.clone({
                    setHeaders: {
                       'ACCESS-TOKEN': currentUser.accessToken,
                       'USER_ID' : currentUser.userId
                    }
                });
            }
        }

        return next.handle(request);
    }
}
