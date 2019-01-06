import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) { }

    getCurrentUser() {
        return this.http.get(`${this.baseUrl}/web/me/`);
    }

    // getWeatherState(city: string): Subject<string> {
    //     const dataSubject = new Subject<string>();
    //     this.http.get(
    //         `https://api.openweathermap.org/data/2.5/weather?q=${city}&APPID=952d6b1a52fe15a7b901720074680562`)
    //         .subscribe((data) => {
    //             dataSubject.next(data['weather'][0].main);
    //         });
    //     return dataSubject;
    // }
}