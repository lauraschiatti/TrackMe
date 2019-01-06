import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
import { AuthenticationService } from '../_services';
import {Observable} from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
    iForm: FormGroup;
    tpForm: FormGroup;
    iFormSubmitted = false;
    tpFormSubmitted = false;
    bloodTypes = [
        {value: 'A_POSITIVE', label: 'A+'},
        {value: 'A_NEGATIVE', label: 'A-'},
        {value: 'B_POSITIVE', label: 'B+'},
        {value: 'B_NEGATIVE', label: 'B-'},
        {value: 'AB_POSITIVE', label: 'AB+'},
        {value: 'AB_NEGATIVE', label: 'AB-'},
        {value: 'ZERO_POSITIVE', label: 'O+'},
        {value: 'ZERO_NEGATIVE', label: 'O-'}
    ];

    constructor(
        private iFormBuilder: FormBuilder,
        private tpFormBuilder: FormBuilder,
        private authService: AuthenticationService,
        // private route: ActivatedRoute,
        // private router: Router,
    ) {}

    ngOnInit() {
        this.iForm = this.iFormBuilder.group({
            name: ['', Validators.required],
            gender: ['', Validators.required],
            birthDate: ['', Validators.required],
            ssn: ['', Validators.required],
            weight: ['', Validators.required],
            height: ['', Validators.required],
            bloodType: ['', Validators.required],
            address: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
        });

        this.tpForm = this.tpFormBuilder.group({
            companyname: ['', Validators.required],
            taxcode: ['', Validators.required],
            phone: ['', Validators.required],
            certificate: ['', Validators.required],
            emailtp: ['', [Validators.required, Validators.email]],
            passwordtp: ['', [Validators.required, Validators.minLength(6)]],

            // TPConfiguration
            individualpushurl: [''],
            bulkpushurl: [''],
            notificationurl: ['']
        });
    }

    // convenience getter for easy access to form fields
    get iControls() { return this.iForm.controls; }
    get tpControls() { return this.tpForm.controls; }

    onSubmitIndividuals() {
        this.iFormSubmitted = true;

        // stop here if form is invalid
        if (this.iForm.invalid) {
            return;
        }

        const address = this.iControls.address.value.split(',');

        const individual = {
            'name':  this.iControls.name.value,
            'ssn': this.iControls.ssn.value,
            'weight': this.iControls.weight.value,
            'height': this.iControls.height.value,
            'birthDate': this.iControls.birthDate.value,
            'gender': this.iControls.gender.value,
            'address': {
                'city': address[0],
                'province': address[1],
                'country': address[2]
            },
            'bloodType': this.iControls.bloodType.value,
            'email': this.iControls.email.value,
            'password': this.iControls.password.value
        };

        if (this.iForm.valid) {
            this.authService
                .signupIndividual(individual)
                .subscribe(
                  data => {
                        const user = {
                          'userId': data['userId'],
                          'accessToken': data['accessToken'],
                          'role': 'INDIVIDUAL'
                        };

                        this.authService.setCurrentUser(user);
                      console.log('ok');
                      // if(data.role == 'INDIVIDUAL'){
                      //     this.router.navigate(['/individual/{data.userId}/dashboard']);
                      // }else if(data.role == 'THIRD_PARTY'){
                      //     this.router.navigate(['/company/{data.userId}/dashboard']);
                      // }else{
                      //     return;
                      // }

                  },
                  error => {
                      console.log('Error', error);
                  }
            );
        }
    }

    // onSubmitThirdParties() {
    //     this.tpFormSubmitted = true;
    //
    //     // stop here if form is invalid
    //     if (this.tpForm.invalid) {
    //         return;
    //     }
    //     alert('THIRD PARTY!! :-)\n\n' + JSON.stringify(this.tpForm.value));
    // }

}
