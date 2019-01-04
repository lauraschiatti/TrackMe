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
    individualForm: FormGroup;
    submitted = false;
    bloodTypes = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

    constructor(
        private formBuilder: FormBuilder,
        // private route: ActivatedRoute,
        // private router: Router,
        // private individualnService: IndividualService,
    ) {
        // this.individualForm = this.formBuilder.group({
        //     bloodTypes: this.bloodTypes
        // });
    }

    ngOnInit() {
        this.individualForm = this.formBuilder.group({
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

        // this.returnUrl =  '/';
    }

    // convenience getter for easy access to form fields
    get f() { return this.individualForm.controls; }

    onSubmit() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.individualForm.invalid) {
            return;
        }

        alert('SUCCESS!! :-)\n\n' + JSON.stringify(this.individualForm.value));

        // http.get(baseUrl + 'api/SampleData/GetSummaries').subscribe(result => {
        //     this.summaries = result.json() as any[];
        // }, error => console.error(error));
    }

}
