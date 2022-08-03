import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { StudentComponent } from './student/student.component';
import { AdminComponent } from './admin/admin.component';
import { ExamComponent } from './exam/exam.component';
import { StudentResultComponent } from './student-result/student-result.component';
import { AdminResultComponent } from './admin-result/admin-result.component';
import { AdminQuestionComponent } from './admin-question/admin-question.component';
import { AddComponent } from './question/add/add.component';
import { AdminSortComponent } from './admin-sort/admin-sort.component';
import { AdminFilterComponent } from './admin-filter/admin-filter.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'student', component: StudentComponent },
  { path: 'admin', component: AdminComponent },
  { path: 'student/exam', component:ExamComponent},
  { path: 'student/student-result', component:StudentResultComponent},
  { path: 'admin/admin-result', component:AdminResultComponent},
  { path: 'admin/admin-question', component:AdminQuestionComponent},
  { path: 'admin/add-question', component:AddComponent},
  { path: 'admin/admin-sort', component:AdminSortComponent},
  { path: 'admin/admin-filter', component:AdminFilterComponent},
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }