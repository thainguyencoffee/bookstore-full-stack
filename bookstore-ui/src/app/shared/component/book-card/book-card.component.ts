import {Component, inject, Input, ViewChild} from '@angular/core';
import {Book} from "../../model/book";
import {MatCardModule} from "@angular/material/card";
import {CommonModule} from "@angular/common";
import {RouterLink} from "@angular/router";
import {Observable} from "rxjs";
import {ShoppingCart} from "../../model/shopping-cart";
import {MatButton} from "@angular/material/button";
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {BookQuantityComponent} from "../book-quantity/book-quantity.component";

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    RouterLink,
    MatButton,
    BookQuantityComponent
  ],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.css'
})
export class BookCardComponent {
  @Input("book") book: Book | undefined;
  @Input("shopping-cart") shoppingCart$: Observable<ShoppingCart | undefined> | undefined;

  private shoppingCartService = inject(ShoppingCartService);

  addToCart(cartId: string) {
    if (this.book && this.book.isbn) {
      this.shoppingCartService.updateCart({cartId: cartId, isbn: this.book.isbn, quantity: 1})
    } else {
      console.log("Book#isbn is error")
    }
  }
}
