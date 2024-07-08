import {HttpClient} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {User} from "../model/user";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  isAuthenticated = false;
  user: User | undefined;

  authenticate(): Observable<User>{
    return this.http.get<User>(`/user`);
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
