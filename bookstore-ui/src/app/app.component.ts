import {Component, inject, OnInit} from '@angular/core';
import {RouterModule, RouterOutlet} from '@angular/router';
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {AuthService} from "./shared/service/auth.service";
import {User} from "./shared/model/user";
import {map, Observable, shareReplay} from "rxjs";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule, MatNavList} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {CommonModule} from "@angular/common";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    RouterOutlet,
    MatSidenavModule,
    MatToolbarModule,
    MatNavList,
    MatIconModule,
    MatListModule,
    MatCardModule,
    MatButtonModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title = 'bookstore-ui';

  private breakpointObserver = inject(BreakpointObserver);
  private http = inject(HttpClient)
  authService = inject(AuthService);

  user: User | undefined;
  isAuthenticated: boolean = false;
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  ngOnInit(): void {
    this.authService.authenticate().subscribe({
      next: user => {
        this.user = user;
        this.isAuthenticated = true;
      }
    })
    this.http.get('/api/books').subscribe(data => console.log(data))
  }

  logInClicked(): void {
    this.authService.login();
  }

}
