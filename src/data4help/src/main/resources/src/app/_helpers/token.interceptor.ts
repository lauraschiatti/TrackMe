import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
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

        const currentUser = this.authenticationService.currentUserValue;

        if (currentUser && currentUser.accessToken) {

            // check if token is valid --> catch error

            request = request.clone({
                headers: request.headers
                    .set('ACCESS-TOKEN', currentUser.accessToken)
                    .set('USER_ID', currentUser.userId)
            });

        }

        return next.handle(request);
    }
}
