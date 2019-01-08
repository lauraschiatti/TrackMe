import { Component, OnInit } from '@angular/core';
import { AuthenticationService, UserService } from '../_services';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user = '';
  role = '';

  constructor(
      private userService: UserService,
      private authenticationService: AuthenticationService
  ) {
      this.role = this.authenticationService.currentUserValue.role;
  }

  ngOnInit() {
      this.userService
          .getCurrentUserInfo()
          .subscribe(
              data => {
                  this.user = data['data'];
                  // console.log('user', this.role);
              },
              error => {
                  console.log('error', error);
              });
  }

}
