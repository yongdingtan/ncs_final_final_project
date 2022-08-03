import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Question } from '../model/question';
import { QuestionStudent } from '../model/question-student';

const baseUrl = 'http://localhost:8089/admin/question/';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  constructor(private httpClient:HttpClient) { }

  addQuestion(question:Question):
  Observable<any>
  {
    return this.httpClient.post(baseUrl+'create', question);
  }

  getQuestionByID(id:number):
  Observable<any>
  {
    return this.httpClient.get(baseUrl+'${id}');
  }

  getAllQuestions(): 
  Observable<Question[]>
  {
    return this.httpClient.get<Question[]>(baseUrl+'read');
  }

  getAllQuestionsByCategory(category:string):
  Observable<Question[]>
  {
    return this.httpClient.get<Question[]>(baseUrl+'category?category='+category);
  }

  updateQuestion(questionNumber:number, question:Question):
  Observable<any>
  {
    return this.httpClient.put(baseUrl+'edit/'+questionNumber, question);
  }

  deleteQuestion(questionNumber:number):
  Observable<any>
  {
    return this.httpClient.delete(baseUrl+'delete/'+ questionNumber);
  }

  getExamQuestionsBasedOnCategoryAndLevel(category:string, level:string):
  Observable<QuestionStudent[]>
  {
    return this.httpClient.get<QuestionStudent[]>('http://localhost:8089/question/exam/attempt?category='+category+'&level='+level);
  }

  getQuestionsBasedOnCategoryAndLevel(category:string, level:string):
  Observable<Question[]>
  {
    return this.httpClient.get<Question[]>('http://localhost:8089/admin/question/category?category='+category+'&level='+level);
  }

}
