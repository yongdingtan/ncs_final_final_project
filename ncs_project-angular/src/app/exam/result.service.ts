import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../model/user';

const baseUrl ='http://localhost:8089/result';

@Injectable({
  providedIn: 'root'
})
export class ResultService {

  constructor(private httpClient:HttpClient) { }

  getResultsBasedOnParams(date:Date, category:string, level:string, id:number):
  Observable<User[]>
  {
    return this.httpClient.get<User[]>(baseUrl+'/results'+date+category+level+id);
  }

  

}
