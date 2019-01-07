import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
    providedIn: 'root' // this service should be created by the root application injector.
})
export class SearchService {

    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) { }

    search(ssn?, params?) {

        if (ssn) {
            let httpParams = new HttpParams().set('ssn', '1');

            return this.http.get(`${this.baseUrl}/web/search`, { params: httpParams });
        }

        if (params) {}

        // Bulk data
        // {
        //  "country" : "12121212",
        // 	"province": "asdasd",
        // 	"city" : "12121212",
        // 	"gender" : "12121212",
        // 	"minAge": 21,
        // 	"maxAge": 22,
        // 	"bloodType": "A_POSITIVE"
        // }

    }

}
