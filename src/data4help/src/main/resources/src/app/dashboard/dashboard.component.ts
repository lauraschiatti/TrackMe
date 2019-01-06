import { Component, OnInit } from '@angular/core';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {

  private data;
  constructor(
      private userService: UserService
  ) { }

  ngOnInit() {
      this.userService
          .getCurrentUser()
          .subscribe(
              data => {
                  this.data = {
                      name: data['name'],
                      ssn:  data['ssn']
                  };
                  console.log('data', this.data);
              },
              error => {
                  console.log('error', error);
              });

  }

}
