import {Component, inject, OnInit} from '@angular/core';
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {CommonModule} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {distinctUntilChanged, map, Observable} from "rxjs";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {BookQuantityComponent} from "../../shared/component/book-quantity/book-quantity.component";
import {RouterModule} from "@angular/router";
import {BookService} from "../../shared/service/book.service";
import {Book} from "../../shared/model/book";
import {SnackbarService} from "../../shared/service/snackbar.service";
import {CustomError} from "../../shared/model/api-error";
import {BookCardComponent} from "../../shared/component/book-card/book-card.component";
import {ShoppingCart} from "../../shared/model/shopping-cart";

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatGridList,
    MatGridTile,
    BookQuantityComponent,
    RouterModule,
    BookCardComponent
  ],
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.css'
})
export class ShoppingCartComponent implements OnInit{
  private shoppingCartService = inject(ShoppingCartService);
  shoppingCart: ShoppingCart | undefined;

  ngOnInit(): void {
    this.shoppingCartService.$shoppingCart
      .subscribe(cart => this.shoppingCart = cart)
  }

}
