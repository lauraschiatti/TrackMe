import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators} from '@angular/forms';
// import { HttpClient } from '@angular/common/http';

// import { Router } from '@angular/router';
// import { IndividualService } from '../_services/individual.service';

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
    bloodTypes = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

    constructor(
        private iFormBuilder: FormBuilder,
        private tpFormBuilder: FormBuilder,
        // private route: ActivatedRoute,
        // private router: Router,
        // private individualService: IndividualService,
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

        // http.get(baseUrl + 'api/SampleData/GetSummaries').subscribe(result => {
        //     this.summaries = result.json() as any[];
        // }, error => console.error(error));
    }

    onSubmitThirdParties() {
        this.tpFormSubmitted = true;

        // stop here if form is invalid
        if (this.tpForm.invalid) {
            return;
        }
        alert('THIRD PARTY!! :-)\n\n' + JSON.stringify(this.tpForm.value));

        // http.get(baseUrl + 'api/SampleData/GetSummaries').subscribe(result => {
        //     this.summaries = result.json() as any[];
        // }, error => console.error(error));
    }

}
