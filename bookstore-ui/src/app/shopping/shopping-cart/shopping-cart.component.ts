import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {CommonModule} from "@angular/common";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {forkJoin, Subject, switchMap, takeUntil} from "rxjs";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {BookQuantityComponent} from "../../shared/component/book-quantity/book-quantity.component";
import {RouterModule} from "@angular/router";
import {BookService} from "../../shared/service/book.service";
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
export class ShoppingCartComponent implements OnInit, OnDestroy {
  private shoppingCartService = inject(ShoppingCartService);
  private bookService = inject(BookService);
  shoppingCart: ShoppingCart | undefined;
  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.shoppingCartService.$shoppingCart
      .pipe(
        switchMap(cart => {
          this.shoppingCart = cart;
          const bookDetailObservables = cart!.cartItems.map(item =>
            this.bookService.getBookByIsbn(item.isbn)
          );
          return forkJoin(bookDetailObservables);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe(bookDetails => {
        if (this.shoppingCart) {
          this.shoppingCart.cartItems.forEach((item, index) => {
            item.bookDetail = bookDetails[index];
          });
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
