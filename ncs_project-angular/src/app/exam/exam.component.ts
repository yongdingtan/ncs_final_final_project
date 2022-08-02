import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { QuestionStudent } from '../model/question-student';
import { QuestionService } from './question.service';

@Component({
  selector: 'app-exam',
  templateUrl: './exam.component.html',
  styleUrls: ['./exam.component.css']
})
export class ExamComponent implements OnInit {

  question : QuestionStudent = new QuestionStudent;
  questions: QuestionStudent[] = [];
  
  categories:any=['All Categories','mySQL','Maths', 'Java','Science','Geography'];
  Level:any=['All levels','Basic','Intermediate','Advanced'];

  
	totalScore: number = 0;
	isExamCardShown: boolean = false;
  isQuizTitleShown: boolean = true;
  isScoreShown: boolean = true;

  constructor(private questionService:QuestionService, private router: Router) { }

  ngOnInit(): void {
  }

  private getExamQuestions()
  {
    this.questionService.getExamQuestionsBasedOnCategoryAndLevel().subscribe((questions:QuestionStudent[])=>
    {
      this.questions = questions;
    })
  }

  startQuiz() {

    this.getExamQuestions();
		this.isExamCardShown = true;
    this.isQuizTitleShown = false;

	}

  getResults()
  {
    this.questionService.
  }

  submitTest() {
    let map = new Map();
		for (let i = 0; i < this.questions.length; i++) {
			if ("questionSelected" in this.questions[i] && (this.questions[i]["questionSelected"] != null)) {
        map.set(this.questions[i].questionNumber, this.questions[i]["questionSelected"]);
			}
		}
    this.isExamCardShown=false;
    this.isScoreShown=false;

    this.

	}

}
