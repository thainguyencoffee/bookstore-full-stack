import {Component, inject, OnInit} from '@angular/core';
import {RouterModule, RouterOutlet} from '@angular/router';
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {AuthService} from "./shared/service/auth.service";
import {map, Observable, shareReplay} from "rxjs";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule, MatNavList} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {CommonModule} from "@angular/common";
import {ShoppingCartService} from "./shared/service/shopping-cart.service";

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
export class AppComponent implements OnInit {
  title = 'bookstore-ui';

  private breakpointObserver = inject(BreakpointObserver);
  authService = inject(AuthService);
  private shoppingCartService = inject(ShoppingCartService);

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  isSmall$: Observable<boolean> = this.breakpointObserver.observe([Breakpoints.Small, Breakpoints.Medium, Breakpoints.XSmall])
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  ngOnInit(): void {
    this.authService.authenticate().subscribe({
      next: user => {
        this.authService.user = user;
        this.authService.isAuthenticated = true;
        this.shoppingCartService.getShoppingCart();
      }
    })
  }

  logInClicked(): void {
    this.authService.login();
  }

}
