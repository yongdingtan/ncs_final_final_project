import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../admin/admin.service';
import { TestScoreService } from '../exam/test-score.service';
import { TestScore } from '../model/test-score';

@Component({
  selector: 'app-admin-result',
  templateUrl: './admin-result.component.html',
  styleUrls: ['./admin-result.component.scss']
})
export class AdminResultComponent implements OnInit {

  testScores: TestScore[] = [];
  Categories: any = ['All Categories', 'mySQL', 'Maths', 'Java', 'Science', 'Geography'];
  Level: any = ['All levels', 'Basic', 'Intermediate', 'Advanced'];
  apiResponse: TestScore[] = [];
  allTestScores: any = [];
  students:Number[]=[];
  constructor(private testScoreService: TestScoreService,private adminService: AdminService, private router: Router) { }

  ngOnInit(): void {

    this.getAllStudentID();

    this.adminService.getTestResults().subscribe((response: any) => {
      this.apiResponse = response;
      this.allTestScores = response;
    })


  }

  updateTestScore(testScore?: TestScore): void {

  }

  deleteTestScore(testScore?: TestScore): void {

  }

  getAllStudentID()
  {
    this.adminService.getAllStudentID().subscribe((response:Number[])=>
    {
      this.students=response;
    })
  }

  applyFilter() {
    let studentid = (<HTMLInputElement>document.getElementById("studentid")).value;
    let category = (<HTMLInputElement>document.getElementById("category")).value;
    let level = (<HTMLInputElement>document.getElementById("level")).value;
    this.testScoreService.getTestScoresBasedOnCategoryAndLevel(studentid, category, level).subscribe((testScores: TestScore[]) => {
      this.apiResponse = testScores;
    })
  }
}
