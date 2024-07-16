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
import {PurchaseOrderService} from "./shared/service/purchase-order.service";
import {AuthState} from "./shared/model/auth-state";

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
  authState: AuthState | undefined;
  private shoppingCartService = inject(ShoppingCartService);
  private orderService = inject(PurchaseOrderService);

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
    this.authService.authenticate();
    this.authService.$authState.subscribe({
      next: authState => {
        this.authState = authState;
        this.shoppingCartService.getShoppingCart();
        // save orders
        if (this.authState.user) {
          const username = this.authState.user.username;
          this.saveOrderWhenLogin(username);
        }
      }
    })
  }

  saveOrderWhenLogin(username: string) {
    const ordersLocalStorage: {id: string}[] = this.orderService.getOrdersFromLocalStorage();
    if (ordersLocalStorage.length > 0) {
      for (const ordersLocalStorageElement of ordersLocalStorage) {
        console.log(ordersLocalStorageElement)
        this.orderService.getOrderByOrderId(ordersLocalStorageElement.id, true).subscribe({
          next: order => {
            order.createdBy = username;
            order.lastModifiedBy = username;
            console.log("app component update order")
            this.orderService.updateOrder(order).subscribe();
            this.orderService.removeOrdersFromLocalStorage();
          }
        })
      }
    }
  }

  logInClicked(): void {
    this.authService.login();
  }
}
