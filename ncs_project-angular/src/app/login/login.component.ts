import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../model/user';
import { AuthService } from '../_services/auth.service';
import { StorageService } from '../_services/storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  user:User =new User();

  
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles = '';

  constructor(private router: Router,
    private authService: AuthService, private storageService: StorageService) { }

  ngOnInit() {
    if (this.storageService.isLoggedIn()) {
    this.isLoggedIn = true;
    this.roles = this.storageService.getUser().roles;
  }
  }

  onSubmit(form: NgForm): void {
    this.user = form.value;
    this.authService.login(this.user).subscribe({
      next: data => {
        this.storageService.saveUser(data);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.storageService.getUser().roles;
        this.reloadPage();
      },
      error: err => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    });
  }
  reloadPage(): void {
    window.location.reload();
  }
}