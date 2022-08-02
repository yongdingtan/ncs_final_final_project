import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TestScore } from '../model/test-score';
import { User } from '../model/user';

const baseUrl = 'http://localhost:8089/admin/';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private httpClient:HttpClient) { }

  getUserById(userId:number):
  Observable<any>
  {
    return this.httpClient.get(baseUrl+'/user/read/'+userId);
  }

  editUserById(userId:number, user:User):
  Observable<any>
  {
    return this.httpClient.put(baseUrl+'/user/edit/'+userId, user);
  }

  deleteUserById(userId:number):
  Observable<any>
  {
    return this.httpClient.delete(baseUrl+'/user/delete/'+userId);
  }

  filterStudentsByTheirAverageTestScore():
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/student/filter');
  }

  sortStudentsByTheirTestCategory(category:string):
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/student/sort'+category);
  }

  getAllUserByRoles(role:string):
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/user/roles/'+role);
  }

  getTestResults():
  Observable<TestScore[]>
  {
    return this.httpClient.get<TestScore[]>(baseUrl+'/results');
  }

}
