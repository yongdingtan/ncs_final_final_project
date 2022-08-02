import { Component, OnInit } from '@angular/core';
import { AuthService } from '../_services/auth.service';
import { User } from '../model/user';
import { NgForm } from '@angular/forms';
@Component({
  selector: 'app-register-user',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  isValidFormSubmitted = false;
  user:User =new User();
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService) { }
  ngOnInit(): void {
  }
  onSubmit(form: NgForm){
    this.isValidFormSubmitted = false;
    if (form.invalid) {
       return;
    }
    this.isValidFormSubmitted = true;
    this.user = form.value;
    this.authService.register(this.user).subscribe({
      next: data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      error: err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    });
    this.user = new User();
    form.resetForm();
  }
}