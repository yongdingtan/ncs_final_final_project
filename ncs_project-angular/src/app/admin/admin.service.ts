import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StudentFilterTestscore } from '../model/student-filter-testscore';
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

  sortStudentsByTheirAverageTestScore(category:string, level:string):
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/student/sort?category='+category+'&level='+level);
  }

  sortStudentsByTheirTestCategory(category:string):
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/student/sort?category='+category);
  }

  getAllUserByRoles(role:string):
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/user/roles/'+role);
  }

  getAllStudentID():
  Observable<Number[]>
  {
    return this.httpClient.get<Number[]>('http://localhost:8089/admin/student/getallstudentid');
  }

  getTestResults():
  Observable<TestScore[]>
  {
    return this.httpClient.get<TestScore[]>(baseUrl+'/results');
  }

  filterStudentsByTheirAverageTestScore(category:string, level:string):
  Observable<StudentFilterTestscore[]>
  {
    return this.httpClient.get<StudentFilterTestscore[]>('http://localhost:8089/admin/student/filter?category='+category+'&level='+level);
  }

  getAllQuestionCategory():
  Observable<string[]>
  {
    return this.httpClient.get<string[]>('http://localhost:8089/admin/question/getallcategory');
  }

}
