import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { QuestionService } from 'src/app/exam/question.service';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  styleUrls: ['./add.component.css']
})
export class AddComponent implements OnInit {
  QuestionFormLabel: string = "Add Question";

  addForm: FormGroup = this.formBuilder.group({})
    constructor(
      private formBuilder: FormBuilder,
      private router: Router,
      private questionService: QuestionService 
    ) {}
  
    ngOnInit(): void {
      this.addForm = this.formBuilder.group({
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
  
  onSubmit()
  {
    this.questionService.addQuestion(this.addForm?.value).subscribe
    ((data)=>
    {console.log("Question saved successfully"+ data);
    this.router.navigate(['admin/admin-question']);
    });
    
  }

}
