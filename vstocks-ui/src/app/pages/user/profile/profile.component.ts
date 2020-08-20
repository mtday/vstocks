import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { User } from '../../../models/user';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  public user: User;
  private userObservable: Observable<User>;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userObservable = this.userService.getCurrentUser();
    this.userObservable.subscribe(user => {
      this.user = user;
    });
  }

  profileSave(): void {
  }
}
