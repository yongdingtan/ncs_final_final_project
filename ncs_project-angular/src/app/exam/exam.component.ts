import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPseudoCheckbox } from '@angular/material/core';
import { Router } from '@angular/router';
import { map, values } from 'lodash';
import { QuestionStudent } from '../model/question-student';
import { StudentTestscore } from '../model/student-testscore';
import { StudentService } from '../student/student.service';
import { StorageService } from '../_services/storage.service';
import { QuestionService } from './question.service';

@Component({
  selector: 'app-exam',
  templateUrl: './exam.component.html',
  styleUrls: ['./exam.component.css']
})
export class ExamComponent implements OnInit {

  currentUser: any;

  question: QuestionStudent = new QuestionStudent;
  questions: QuestionStudent[] = [];

  categories: any = ['All Categories', 'mySQL', 'Maths', 'Java', 'Science', 'Geography'];
  Level: any = ['All levels', 'Basic', 'Intermediate', 'Advanced'];


  totalScore: number = 0;
  isQuizTitleShown: boolean = true;
  isExamCardShown: boolean = false;
  isScoreShown: boolean = false;
  studentTestScore:any=StudentTestscore;
  studentsAboveYou:number = 0;
  studentsBeneathYou:number = 0;

  constructor(private storageService: StorageService, private studentService: StudentService, private questionService: QuestionService, private router: Router) { }

  ngOnInit(): void {
  }

  private getExamQuestions() {
    this.questionService.getExamQuestionsBasedOnCategoryAndLevel().subscribe((questions: QuestionStudent[]) => {
      this.questions = questions;
    })
  }

  startQuiz() {

    this.getExamQuestions();
    this.isExamCardShown = true;
    this.isQuizTitleShown = false;

  }

  submitTest() {
    let map = new Map<number, string>;
    for (let i = 0; i < this.questions.length; i++) {
      if ("questionSelected" in this.questions[i] && (this.questions[i]["questionSelected"] != null)) {
        map.set(this.questions[i].questionNumber, this.questions[i]["questionSelected"]!);
      }
    }
    const convMap:any = {};
    map.forEach((values:string,key:number)=> convMap[key]=values);
    this.isExamCardShown = false;
    this.isScoreShown = true;
    this.currentUser = this.storageService.getUser();

    this.studentService.answerExamQuestions(this.currentUser.username, convMap).subscribe((response: any)=>
    {

      this.totalScore=response.marks;
      this.studentsAboveYou=response.studentsAboveYou;
      this.studentsBeneathYou=response.studentsBeneathYou;
    });
  }

}
