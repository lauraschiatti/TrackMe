import { Component, OnInit } from '@angular/core';
// import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { User } from '../_models/user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  submitted = false;
  // returnUrl: string;

  constructor(
    private formBuilder: FormBuilder
    // private route: ActivatedRoute,
    // private router: Router,
    // private authenticationService: AuthenticationService,
    // private alertService: AlertService
  ) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    // reset login status
    // this.authenticationService.logout();

    // get return url from route parameters or default to '/'
    // this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  // convenience getter for easy access to form fields
  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    alert(this.f.email.value);

    // user = new User(this.f.email.value, this.f.password.value);


    //   this.authenticationService.login(this.f.username.value, this.f.password.value)
  //     .pipe(first())
  //     .subscribe(
  //       data => {
  //         this.router.navigate([this.returnUrl]);
  //       },
  //       error => {
  //         this.alertService.error(error);
  //         this.loading = false;
  //       });
  }
}
