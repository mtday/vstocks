import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { User } from '../../../models/models';
import { UserService } from '../../../services/user.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-user-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  public user: User;
  public errorMessage: string;

  public profileSaveForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService.getUser().subscribe(user => {
      this.user = user;
      this.profileSaveForm.controls.username.setValue(user.username);
      this.profileSaveForm.controls.displayName.setValue(user.displayName);
    });

    this.profileSaveForm = this.formBuilder.group({
      username: '',
      displayName: ''
    });
  }

  profileSave(): boolean {
    const username: string = this.profileSaveForm.controls.username.value.trim();
    const displayName: string = this.profileSaveForm.controls.displayName.value.trim();
    this.userService.putUser(username, displayName)
      .pipe(
        catchError(error => {
          this.errorMessage = error;
          return of(null);
        })
      )
      .subscribe(user => {
        if (user) {
          this.user = user;
          this.profileSaveForm.controls.username.setValue(user.username);
          this.profileSaveForm.controls.displayName.setValue(user.displayName);
          this.errorMessage = null;
        }
      })
    return false; // prevent page refresh
  }

  checkUsername(): void {
    const username: string = this.profileSaveForm.controls.username.value.trim();
    if (username.length > 2 && (this.user == null || username !== this.user.username)) {
      this.userService.checkUsername(username).subscribe(usernameCheck => {
        this.errorMessage = usernameCheck.message;
      });
    } else {
      this.errorMessage = null;
    }
  }
}
