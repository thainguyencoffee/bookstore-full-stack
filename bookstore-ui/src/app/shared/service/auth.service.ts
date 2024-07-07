import {HttpClient} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User} from "../model/user";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);

  authenticate(): Observable<User> {
    return this.http.get<User>(`/user`);
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
