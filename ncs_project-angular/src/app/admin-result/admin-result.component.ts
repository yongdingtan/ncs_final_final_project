import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../admin/admin.service';
import { TestScore } from '../model/test-score';

@Component({
  selector: 'app-admin-result',
  templateUrl: './admin-result.component.html',
  styleUrls: ['./admin-result.component.scss']
})
export class AdminResultComponent implements OnInit {

  testScores:any=[];
  Categories:any=['All Categories','mySQL','Maths', 'Java','Science','Geography'];
  Level:any=['All levels','Basic','Intermediate','Advanced'];
  apiResponse:any = [];
  allTestScores:any=[];
  constructor(private adminService:AdminService, private router:Router) { }

  ngOnInit():void {
    
    this.adminService.getTestResults().subscribe((response:any)=>
    {
      this.apiResponse = response;
      this.allTestScores=response;
    })

  }

  updateTestScore(testScore?:TestScore):void{

  }

  deleteTestScore(testScore?:TestScore):void{

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
