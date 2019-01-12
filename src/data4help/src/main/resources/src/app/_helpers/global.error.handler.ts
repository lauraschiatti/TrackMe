import {ErrorHandler, Injectable} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';

import {Error} from '../_models';

@Injectable()
export class GlobalErrorHandler implements GlobalErrorHandler {
    handleError(httpError: any) {
        if (httpError instanceof HttpErrorResponse) {
            // console.error('Backend returned status code: ', httpError.status);
            // console.error('Response body:', httpError.message);

            const error = new Error();
            error.code = httpError.error.code;
            error.message = httpError.error.message;
            return error;
        } else {
            // A client-side or network error occurred.
            console.error('An error occurred:', httpError.message);
        }
    }
}
