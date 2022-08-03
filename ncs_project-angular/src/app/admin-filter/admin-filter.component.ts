import { Component, OnInit } from '@angular/core';
import { AdminService } from '../admin/admin.service';
import { StudentFilterTestscore } from '../model/student-filter-testscore';

@Component({
  selector: 'app-admin-filter',
  templateUrl: './admin-filter.component.html',
  styleUrls: ['./admin-filter.component.css']
})
export class AdminFilterComponent implements OnInit {
  apiResponse:any = [];
  allCategories: string[] = [];

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getAllQuestionCategory().subscribe((response: string[]) => {
      this.allCategories = response;
    })
  }

  applyFilter() {
    let category = (<HTMLInputElement>document.getElementById("category")).value;
    let level = (<HTMLInputElement>document.getElementById("level")).value;
    this.adminService.filterStudentsByTheirAverageTestScore(category, level).subscribe((testScore: StudentFilterTestscore[]) => {
      this.apiResponse = testScore;
    })
  }

}
