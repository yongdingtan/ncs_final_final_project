import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { QuestionService } from '../exam/question.service';
import { Question } from '../model/question';

@Component({
  selector: 'app-admin-question',
  templateUrl: './admin-question.component.html',
  styleUrls: ['./admin-question.component.css']
})
export class AdminQuestionComponent implements OnInit {

  editForm: FormGroup = this.formBuilder.group({})

  questions:any=[];
  Categories:any=['All Categories','mySQL','Maths', 'Java','Science','Geography'];
  marks:any=['All marks','1','2','3'];
  apiResponse:any = [];
  allQuestions:any=[];

  //Pagination parameters
  
  p: number = 1;
  count: number = 20;
  questionNumber: any;
  clickedIndex: number | undefined;


  constructor(
    private formBuilder: FormBuilder,private questionService:QuestionService, private router:Router) { }

  ngOnInit(): void {
    this.questionService.getAllQuestions().subscribe((response:any)=>
    {
      this.apiResponse=response;
      this.allQuestions=response;
    })

    this.editForm = this.formBuilder.group({
      questionNumber:['',[Validators.required]],
      questionString:['',[Validators.required]],
      questionCategory:['',[Validators.required]],
      questionMarks:['',[Validators.required]],
      questionOptionOne:['',[Validators.required]],
      questionOptionTwo:['',[Validators.required]],
      questionOptionThree:['',[Validators.required]],
      questionOptionFour:['',[Validators.required]],
      correctAnswer:['',[Validators.required]],
    });

  }

  moveToAddQuestion(){
    this.router.navigate(["/admin/add-question"]);
 }

  updateQuestion()
  {
    this.questionService.updateQuestion(this.editForm?.value.questionNumber,this.editForm?.value).subscribe();
  }

  deleteQuestion(questionNumber:number):void{
    this.questionService.deleteQuestion(questionNumber).subscribe();
  }

  openEdit(question:Question, pIndex:number)
  {
    this.clickedIndex = pIndex;
  }

  applyFilter(event:any)
  {
    if(event.target.value === "All Categories" || event.target.value === "All marks")
    {
      this.apiResponse = this.allQuestions;
    }else{
      this.apiResponse = this.allQuestions.filter((question:Question) => question.questionCategory===event.target.value || Number(question.questionMarks)===Number(event.target.value));
    }
  }

}