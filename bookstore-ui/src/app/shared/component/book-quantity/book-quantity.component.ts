import {Component, inject, Input, OnInit} from '@angular/core';
import {Book} from "../../model/book";
import {Observable} from "rxjs";
import {ShoppingCart} from "../../model/shopping-cart";
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {AsyncPipe, CommonModule} from "@angular/common";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatDialog} from "@angular/material/dialog";
import {QuestionDialogComponent} from "../dialog/question-dialog/question-dialog.component";

@Component({
  selector: 'app-book-quantity',
  standalone: true,
  imports: [
    CommonModule,
  ],
  templateUrl: './book-quantity.component.html',
  styleUrl: './book-quantity.component.css'
})
export class BookQuantityComponent implements OnInit{
  @Input("book") book: Book | undefined;
  @Input("shopping-cart") shoppingCart$: Observable<ShoppingCart | undefined> | undefined;
  shoppingCart: ShoppingCart | undefined;
  private shoppingCartService = inject(ShoppingCartService);
  private dialog = inject(MatDialog);

  ngOnInit(): void {
    this.shoppingCart$?.subscribe(cart => this.shoppingCart = cart);
  }

  addToCart(cartId: string) {
    if (this.book && this.book.isbn) {
      this.shoppingCartService.updateCart({cartId: cartId, isbn: this.book.isbn, quantity: 1})
    } else {
      console.log("Book#isbn is error")
    }
  }

  removeFromCart(cartId: string) {
    if (this.shoppingCart && this.book && this.book.isbn) {
      const body = {cartId: this.shoppingCart.id, isbn: this.book.isbn, quantity: -1};
      if (this.shoppingCart.getQuantity(this.book.isbn) - 1 == 0) {
        const dialogRef = this.dialog.open(QuestionDialogComponent, {
          data: {
            title: 'Delete cart item',
            message: 'Do you want to delete this cart item ?'
          }
        });
        const a = false;
        dialogRef.afterClosed().subscribe(result => {
          if (result) this.shoppingCartService.updateCart(body)
        })
      } else {
        this.shoppingCartService.updateCart(body)
      }
    } else {
      console.log("Book#isbn is error")
    }
  }

  protected readonly JSON = JSON;
}
