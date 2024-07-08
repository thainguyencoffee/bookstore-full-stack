import {HttpClient} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from "../model/user";
import {AuthState} from "../model/auth-state";
import {SnackbarService} from "./snackbar.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private snackBarService = inject(SnackbarService);
  private authSubject = new BehaviorSubject<AuthState>({user: undefined, isAuthenticated: false});
  authState$ = this.authSubject.asObservable();

  authenticate() {
    return this.http.get<User>(`/user`).subscribe({
      next: user => {
        this.authSubject.next({
          user: user,
          isAuthenticated: true
        })
      },
      error: err => {
        this.snackBarService.show("Login failure")
        this.authSubject.next({
          isAuthenticated: false,
          user: undefined
        })
      }
    })
  }

  login(): void {
    window.open(`/oauth2/authorization/keycloak`, '_self');
  }

  isEmployee(user: User | undefined): boolean {
    return user ? user.roles.find(role => role === 'employee') !== undefined : false;
  }

  isAdmin(user: User | undefined): boolean {
    return user ? user.roles.find(role => role === 'admin') !== undefined : false;
  }
}
