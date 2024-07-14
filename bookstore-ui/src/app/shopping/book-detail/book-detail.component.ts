import {Component, inject, OnInit} from '@angular/core';
import {Book} from "../../shared/model/book";
import {BookService} from "../../shared/service/book.service";
import {ActivatedRoute, Router} from "@angular/router";
import {CommonModule} from "@angular/common";
import {MatGridList, MatGridTile} from "@angular/material/grid-list";
import {FormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {BookQuantityComponent} from "../../shared/component/book-quantity/book-quantity.component";
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {CartItem, ShoppingCart} from "../../shared/model/shopping-cart";
import {SnackbarService} from "../../shared/service/snackbar.service";

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatGridList,
    MatGridTile,
    FormsModule,
    MatButton,
    BookQuantityComponent
  ],
  templateUrl: './book-detail.component.html',
  styleUrl: './book-detail.component.css'
})
export class BookDetailComponent implements OnInit {

  book: Book | undefined;
  bookService = inject(BookService);
  route = inject(ActivatedRoute);
  router = inject(Router);
  shoppingCartService = inject(ShoppingCartService);
  shoppingCart$ = this.shoppingCartService.$shoppingCart;
  shoppingCart: ShoppingCart | undefined;
  snackbarService = inject(SnackbarService);

  ngOnInit(): void {
    this.shoppingCartService.getShoppingCart();
    this.shoppingCart$.subscribe({
      next: cart => this.shoppingCart = cart
    })
    this.route.paramMap.subscribe(params => {
      const bookIsbn = params.get('isbn');
      if (bookIsbn) {
        this.bookService.getBookByIsbn(bookIsbn).subscribe({
          next: book => this.book = book
        })
      }
    })
  }

  checkout(isbn: string) {
    let selectedItem: CartItem | undefined;
    if (this.shoppingCart) {
      const cartItem = this.shoppingCart?.hasItem(isbn);
      if (cartItem) selectedItem = cartItem
      else {
        this.snackbarService.show("Not existing cart item with isbn " + isbn, "Close")
        return;
      }
    }
    this.router.navigate(['/checkout'], { state: { selectedItem } });
  }

  addToCart(cartId: string) {
    if (this.book && this.book.isbn) {
      this.shoppingCartService.updateCart({
        cartId: cartId,
        isbn: this.book.isbn,
        quantity: 1,
        inventory: this.book.inventory
      })
    } else {
      console.log("Book#isbn is error")
    }
  }


}
