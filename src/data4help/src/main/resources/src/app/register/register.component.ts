import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
// import { SignupService } from '../_services/signup.service';
// import { Router } from '@angular/router';

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
        // private route: ActivatedRoute,
        // private router: Router,
        // private signupService: SignupService,
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
        alert('INDIVIDUAL!! :-)\n\n' + JSON.stringify(this.iForm.value));

        // this.signupService.signupIndividual(
        //     this.iControls.name.value,
        //     this.iControls.gender.value,
        //     this.iControls.birthDate.value,
        //     this.iControls.ssn.value,
        //     this.iControls.weight.value,
        //     this.iControls.height.value,
        //     this.iControls.bloodType.value,
        //     this.iControls.address.value,
        //     this.iControls.email.value,
        //     this.iControls.password.value
        // );

        // this.authenticationService.login(this.f.username.value, this.f.password.value)
        //     .pipe(first())
        //     .subscribe(
        //         data => {
        //             this.router.navigate([this.returnUrl]);
        //         },
        //         error => {
        //             this.alertService.error(error);
        //             this.loading = false;
        //         });

    }

    onSubmitThirdParties() {
        this.tpFormSubmitted = true;

        // stop here if form is invalid
        if (this.tpForm.invalid) {
            return;
        }
        alert('THIRD PARTY!! :-)\n\n' + JSON.stringify(this.tpForm.value));
    }

}
