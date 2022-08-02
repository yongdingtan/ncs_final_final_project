import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const baseUrl = 'http://localhost:8089/student/'

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private httpClient:HttpClient) { }

  answerExamQuestions(username:string, answers:Map<number, string>):
  Observable<any>
  {
    
    return this.httpClient.post(baseUrl+'exam/answer/'+username, answers);
  }

  getTestResults(username:string):
  Observable<any>
  {
    return this.httpClient.get(baseUrl+'results/'+username);
  }

}
