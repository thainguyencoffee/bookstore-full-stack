import {HttpClient} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {User} from "../model/user";
import {BehaviorSubject} from "rxjs";
import {AuthState} from "../model/auth-state";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  authSubject = new BehaviorSubject<AuthState>({user: undefined, isAuthenticated: false});
  $authState = this.authSubject.asObservable();
  isAuthenticated = false;
  user: User | undefined;

  authenticate() {
    this.http.get<User>(`/user`).subscribe({
      next: user => {
        this.user = user;
        this.isAuthenticated = true;
        this.authSubject.next({user: user, isAuthenticated: true});
        return user;
      },
      error: () => {
        this.isAuthenticated = false;
        this.authSubject.next({user: undefined, isAuthenticated: false});
      }
    });
  }

  login(): void {
    window.open(`/oauth2/authorization/keycloak`, '_self');
  }

  isEmployee(): boolean {
    return this.user ? this.user.roles.find(role => role === 'employee') !== undefined : false;
  }

  isAdmin(): boolean {
    return this.user ? this.user.roles.find(role => role === 'admin') !== undefined : false;
  }

  isLoggedIn(): boolean {
    return this.isAuthenticated;
  }
}
