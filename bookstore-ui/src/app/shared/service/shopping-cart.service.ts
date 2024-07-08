import {inject, Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {CartItem, ShoppingCart} from "../model/shopping-cart";
import {HttpClient} from "@angular/common/http";
import {CustomError} from "../model/api-error";
import {SnackbarService} from "./snackbar.service";
import {AuthService} from "./auth.service";
import {AuthState} from "../model/auth-state";

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {
  private shoppingCartSubject = new BehaviorSubject<ShoppingCart | undefined>(undefined);
  public $shoppingCart = this.shoppingCartSubject.asObservable();
  private http = inject(HttpClient)
  private snackBarService = inject(SnackbarService);
  private authService = inject(AuthService);
  private authState: AuthState | undefined;

  constructor() {
    this.authService.authState$
      .subscribe({
        next: authState => this.authState = authState
      })
  }

  getShoppingCart() {
    if (this.authState && this.authState.isAuthenticated) {
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
    } else {
      this.loadShoppingCartFromLocalStorage();
      this.snackBarService.show("Chưa đăng nhập");
    }
  }

  updateCart(body: {
    cartId: string,
    isbn: string,
    quantity: number,
    inventory: number
  }): void {
    if (this.authState && this.authState.isAuthenticated) {
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
    else {
      this.updateLocalCart(body);
    }
  }

  private updateLocalCart(
    body: {
      cartId: string;
      isbn: string;
      quantity: number,
      inventory: number
    }) {
    const shoppingCartLocal = this.loadShoppingCartFromLocalStorage();
    const i = shoppingCartLocal.cartItems.findIndex((item: CartItem) => item.isbn === body.isbn);
    if (i !== -1) {
      const quantityChanged = shoppingCartLocal.cartItems[i].quantity + body.quantity;
      if (quantityChanged <= 0)
        shoppingCartLocal.cartItems.splice(i, 1);
      else if (quantityChanged > body.inventory)
        this.snackBarService.show("Not enough inventory for book with isbn " + body.isbn)
      else
        shoppingCartLocal.cartItems[i].quantity = quantityChanged;
    } else {
      if (body.quantity > body.inventory)
        this.snackBarService.show("Not enough inventory for book with isbn " + body.isbn)
      else {
        const cartItemNew: CartItem = {
          cartId: shoppingCartLocal.id,
          id: 0,
          isbn: body.isbn,
          quantity: body.quantity,
          bookDetail: undefined
        }
        shoppingCartLocal.cartItems.push(cartItemNew)
      }
    }

    shoppingCartLocal.lastModifiedAt = new Date().toISOString();
    this.shoppingCartSubject.next(new ShoppingCart(
      shoppingCartLocal.id,
      shoppingCartLocal.cartItems,
      shoppingCartLocal.createdAt,
      shoppingCartLocal.createdBy,
      shoppingCartLocal.lastModifiedAt,
      shoppingCartLocal.lastModifiedBy
    ));
    this.saveShoppingCartToLocalStorage(shoppingCartLocal);
  }

  loadShoppingCartFromLocalStorage(): ShoppingCart {
    const cartData = localStorage.getItem('shoppingCart');
    if (cartData) {
      const cart = JSON.parse(cartData);
      const shoppingCart = new ShoppingCart(
        cart.id,
        cart.cartItems,
        cart.createdAt,
        cart.createdBy,
        cart.lastModifiedAt,
        cart.lastModifiedBy
      );
      this.shoppingCartSubject.next(shoppingCart);
      return shoppingCart;
    } else {
      console.log('No shopping cart found in local storage, creating a new one.');
      const newCart = this.createEmptyShoppingCart();
      this.shoppingCartSubject.next(newCart);
      this.saveShoppingCartToLocalStorage(newCart);
      return newCart;
    }
  }

  saveShoppingCartToLocalStorage(cart: ShoppingCart) {
    localStorage.setItem('shoppingCart', JSON.stringify(cart));
  }

  createEmptyShoppingCart() {
    const newCart = new ShoppingCart(
      'local-cart-id',
      [],
      new Date().toISOString(),
      'anonymous',
      new Date().toISOString(),
      'anonymous'
    );
    return newCart;
  }
}
