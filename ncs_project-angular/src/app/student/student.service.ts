import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const baseUrl = 'http://localhost:8089/student/'

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private httpClient:HttpClient) { }

  answerExamQuestions(answers:number[]):
  Observable<any>
  {
    return this.httpClient.post(baseUrl+'exam/answer', answers);
  }

  getTestResults(username:string):
  Observable<any>
  {
    return this.httpClient.get(baseUrl+'results/'+username);
  }

}
