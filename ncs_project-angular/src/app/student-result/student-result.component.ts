import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TestScore } from '../model/test-score';
import { StudentService } from '../student/student.service';
import { StorageService } from '../_services/storage.service';


@Component({
  selector: 'app-result',
  templateUrl: './student-result.component.html',
  styleUrls: ['./student-result.component.css']
})
export class StudentResultComponent implements OnInit {

  testScores:any=[];
  Categories:any=['All Categories','mySQL','Maths', 'Java','Science','Geography'];
  Level:any=['All levels','Basic','Intermediate','Advanced'];
  apiResponse:any = [];
  allTestScores:any=[];

  constructor(private studentService:StudentService, private router:Router, private storageService:StorageService) { }

  currentUser: any;
  ngOnInit(): void {
    this.getTestScores();
  }

  getTestScores()
  {
    this.currentUser = this.storageService.getUser();
    this.studentService.getTestResults(this.currentUser.username).subscribe((testScores: TestScore[])=>
    {
      
      this.apiResponse = testScores;
      this.allTestScores=testScores;
    })
  
  }

  applyFilter(event:any) {
    if(event.target.value === 'All Categories' || event.target.value === 'All levels' ) {
        this.apiResponse=this.allTestScores;
    } 
    else {
      this.apiResponse = this.allTestScores.filter((testScore:TestScore) => testScore.category===event.target.value || testScore.level===event.target.value);
    }
}
}
