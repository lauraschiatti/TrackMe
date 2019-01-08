import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';

@Injectable({ providedIn: 'root' })
export class UserService {
    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) { }

    getCurrentUserInfo() {
        return this.http.get(`${this.baseUrl}/web/me/`);
            // .pipe(map(individual => {
            //     return individual;
            // }));
    }

    // patch /web/me/config

}
