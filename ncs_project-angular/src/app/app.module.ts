import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule  } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { AdminComponent } from './admin/admin.component';
import { StudentComponent } from './student/student.component';
import { BasicAuthHtppInterceptorService } from '../_helpers/http.interceptor';
import { AddComponent } from './question/add/add.component';
import { ExamComponent } from './exam/exam.component';
import { StudentResultComponent } from './student-result/student-result.component';
import { AdminResultComponent } from './admin-result/admin-result.component';
import { AdminQuestionComponent } from './admin-question/admin-question.component';
import { AdminUserComponent } from './admin-user/admin-user.component';
import { MaterialComponentsModule } from './material-components/material-components.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import { NgxPaginationModule } from 'ngx-pagination';
import {MatDialogModule} from '@angular/material/dialog';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ProfileComponent,
    AdminComponent,
    StudentComponent,
    AddComponent,
    ExamComponent,
    StudentResultComponent,
    AdminResultComponent,
    AdminQuestionComponent,
    AdminUserComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    MaterialComponentsModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    NgxPaginationModule,
    MatDialogModule
  ],
  providers: [BasicAuthHtppInterceptorService],
  bootstrap: [AppComponent]
})

export class AppModule { }