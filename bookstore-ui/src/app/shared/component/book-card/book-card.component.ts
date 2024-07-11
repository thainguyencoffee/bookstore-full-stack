import {Component, inject, Input, ViewChild} from '@angular/core';
import {Book} from "../../model/book";
import {MatCardModule} from "@angular/material/card";
import {CommonModule} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {interval, Observable, take} from "rxjs";
import {ShoppingCart} from "../../model/shopping-cart";
import {MatButton, MatIconButton} from "@angular/material/button";
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {BookQuantityComponent} from "../book-quantity/book-quantity.component";
import {MatIcon} from "@angular/material/icon";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    RouterLink,
    MatButton,
    BookQuantityComponent,
    MatIcon,
    MatIconButton,
    CdkCopyToClipboard,
    MatTooltip
  ],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.css'
})
export class BookCardComponent {
  @Input("book") book: Book | undefined;
  @Input("shopping-cart") shoppingCart$: Observable<ShoppingCart | undefined> | undefined;

  private shoppingCartService = inject(ShoppingCartService);
  private router = inject(Router);

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

  checkoutNow(book: Book) {
    let singleItem = book;
    this.router.navigate(['/checkout'], { state: { singleItem } });
  }

  onCopyIsbn(event: MouseEvent) {
    event.stopPropagation();
    if (this.copiedContent) {return;}
    this.copiedContent = true;
    this.startCountdown()
  }

  timeRemaining = 0;
  copiedContent = false;
  countdownSubscription: any;

  startCountdown() {
    this.timeRemaining = 5;
    this.countdownSubscription = interval(1000)
      .pipe(take(5))
      .subscribe(() => {
        this.timeRemaining--;
        if (this.timeRemaining === 0) {
          this.copiedContent = false;
          this.countdownSubscription.unsubscribe();
        }
      });
  }

}
