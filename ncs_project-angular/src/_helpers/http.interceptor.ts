import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest,HttpEvent, HttpHandler, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../app/_services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class BasicAuthHtppInterceptorService implements HttpInterceptor {

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (sessionStorage.getItem('username') && sessionStorage.getItem('token')) {
      req = req.clone({
        setHeaders: {
          Authorization: sessionStorage.getItem('token') as string
        }
      })
    }

    return next.handle(req);

  }
}

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: BasicAuthHtppInterceptorService, multi: true },
];