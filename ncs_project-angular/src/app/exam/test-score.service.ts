import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TestScore } from '../model/test-score';

const baseUrl = 'http://localhost:8089/admin/testscore/';

@Injectable({
  providedIn: 'root'
})
export class TestScoreService {

  constructor(private httpClient:HttpClient) { }

  getTestScoreByID(testId:number):
  Observable<any>
  {
    return this.httpClient.get(baseUrl+testId);
  }

  getAllTestScoresByStudentID(userId:number):
  Observable<TestScore[]>
  {
    return this.httpClient.get<TestScore[]>(baseUrl+'/user/'+userId);
  }

  updateTestScore(testId:number, testScore:TestScore):
  Observable<any>
  {
    return this.httpClient.put(baseUrl+'/edit/'+testId, testScore);
  }

  deleteTestScore(testId:number):
  Observable<any>
  {
    return this.httpClient.delete(baseUrl+'/delete/'+testId);
  }

  getTestScoresBasedOnCategoryAndLevel(studentid:string, category:string, level:string):
  Observable<TestScore[]>
  {
    return this.httpClient.get<TestScore[]>('http://localhost:8089/admin/testscore/read?studentid='+studentid+'&category='+category+'&level='+level);
  }

}
