import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable({
    providedIn: 'root' // this service should be created by the root application injector.
})
export class SearchService {

    private baseUrl = environment.baseUrl;
    private httpParams;

    constructor(private http: HttpClient) {
    }

    search(ssn?, params?) {

        if (ssn) {
            this.httpParams = new HttpParams().set('ssn', ssn);
        }

        if (params) {
            this.httpParams =
                new HttpParams()
                    .set('gender', params.gender)
                    .set('bloodType', params.bloodType)
                    .set('minAge', params.minAge)
                    .set('maxAge', params.maxAge)
                    .set('city', params.city)
                    .set('province', params.province)
                    .set('country', params.country);
        }

        return this.http.get(`${this.baseUrl}/web/search`, {params: this.httpParams});
    }

}
