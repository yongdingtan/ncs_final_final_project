import { Component } from '@angular/core';
import { StorageService } from './_services/storage.service';
import { AuthService } from './_services/auth.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private role?: string;
  title = 'ncs_project-angular';
  isLoggedIn = false;
  showAdminBoard = false;
  showStudentBoard = false;
  username?: string;
  constructor(private storageService: StorageService, private authService: AuthService) { }
  ngOnInit(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();
    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.role = user.roles;
      this.showAdminBoard = this.role === 'admin';
      this.showStudentBoard = this.role === 'student';
      this.username = user.username;
    }
  }
  logout(): void {
    this.authService.logOut().subscribe({
      next: res => {
        console.log(res);
        this.storageService.clean();
      },
      error: err => {
        console.log(err);
      }
    });
    
    window.location.reload();
  }
}