import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';

import {AuthenticationService} from '../_services';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    constructor(
        private authenticationService: AuthenticationService,
    ) {
    }

    // include userId accessToken (in localstorage) as headers in any HTTP request that is sent
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        const currentUser = this.authenticationService.currentUserValue;

        if (currentUser && currentUser.accessToken) {
            request = request.clone({
                headers: request.headers
                    .set('ACCESS-TOKEN', currentUser.accessToken)
                    .set('USER_ID', currentUser.userId)
            });

        }

        return next.handle(request);
    }
}
