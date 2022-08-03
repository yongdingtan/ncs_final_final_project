import { Component, OnInit } from '@angular/core';
import { AdminService } from '../admin/admin.service';
import { StudentSortTestscore } from '../model/student-sort-testscore';

@Component({
  selector: 'app-admin-sort',
  templateUrl: './admin-sort.component.html',
  styleUrls: ['./admin-sort.component.css']
})
export class AdminSortComponent implements OnInit {
  apiResponse: any = [];
  allCategories: string[] = [];

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.adminService.getAllQuestionCategory().subscribe((response: string[]) => {
      this.allCategories = response;
    })
  }

  applyFilter() {
    let category = (<HTMLInputElement>document.getElementById("category")).value;
    this.adminService.sortStudentsByTheirTestCategory(category).subscribe((testScore: StudentSortTestscore[]) => {
      this.apiResponse = testScore;
    })
  }

}
