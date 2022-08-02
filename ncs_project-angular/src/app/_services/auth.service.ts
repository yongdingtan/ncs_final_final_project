import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { User } from '../model/user';
import { map } from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;
  
 AUTH_API = 'http://localhost:8089/public/';
 

  constructor(private httpClient: HttpClient) {
  this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser') as string));
  this.currentUser = this.currentUserSubject.asObservable();}

  public get currentUserValue(): User {
    return this.currentUserSubject.value;
}

  login(user: User) {
    console.log(user);
    return this.httpClient.post(
      this.AUTH_API + 'login',
      user,
      httpOptions
    );
  }


  register(user: User): Observable<Object> {
    console.log(user);
    return this.httpClient.post(
      `${this.AUTH_API}` + 'register',
      user
    );
  }
  isUserLoggedIn() {
    let user = sessionStorage.getItem('username')
    //console.log(!(user === null))
    return !(user === null)
  }

  logOut(): Observable<any> {
    return this.httpClient.post(this.AUTH_API + 'logout', {Request, Response}, httpOptions);
  }
}