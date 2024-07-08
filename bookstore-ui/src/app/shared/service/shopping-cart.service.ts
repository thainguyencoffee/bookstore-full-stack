import {inject, Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {ShoppingCart} from "../model/shopping-cart";
import {HttpClient} from "@angular/common/http";
import {CustomError} from "../model/api-error";
import {SnackbarService} from "./snackbar.service";

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {
  private shoppingCartSubject = new BehaviorSubject<ShoppingCart | undefined>(undefined);
  public $shoppingCart = this.shoppingCartSubject.asObservable();
  private http = inject(HttpClient)
  private snackBarService = inject(SnackbarService);

  getShoppingCart() {
    this.http.get<ShoppingCart>('/api/shopping-carts/my-cart').subscribe({
      next: cart => {
        this.shoppingCartSubject.next(new ShoppingCart(
          cart.id,
          cart.cartItems,
          cart.createdAt,
          cart.createdBy,
          cart.lastModifiedAt,
          cart.lastModifiedBy
        ))
      },
      error: err => {
        console.error('Failed to load shopping cart', err)
      }
    });
  }

  updateCart(body: {
    cartId: string,
    isbn: string,
    quantity: number
  }): void {
    this.http.post<ShoppingCart>(`/api/shopping-carts/${body.cartId}/add-to-cart`, body)
      .subscribe({
        next: cart => {
          console.log("Add to cart success")
          this.shoppingCartSubject.next(new ShoppingCart(
            cart.id,
            cart.cartItems,
            cart.createdAt,
            cart.createdBy,
            cart.lastModifiedAt,
            cart.lastModifiedBy
          ))
        },
        error: err => {
          if (err.status === 400 && err.error.errors) {
            err.error.errors.forEach((customError: CustomError) => {
              this.snackBarService.show(customError.message)
            })
          } else {
            this.snackBarService.show(err.message)
          }
        }
      })
  }

}
