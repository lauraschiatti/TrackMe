import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({providedIn: 'root'})
export class UserService {
    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) {
    }

    getCurrentUserInfo() {
        return this.http.get(`${this.baseUrl}/web/me/`);
    }

    updateThirdPartyConfig(config) {
        return this.http.patch(`${this.baseUrl}/web/me/config`, config);
    }
}
