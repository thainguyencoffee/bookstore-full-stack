import {inject, Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {CartItem, ShoppingCart} from "../model/shopping-cart";
import {HttpClient} from "@angular/common/http";
import {CustomError} from "../model/api-error";
import {SnackbarService} from "./snackbar.service";
import {AuthService} from "./auth.service";
import {QuestionDialogComponent} from "../component/dialog/question-dialog/question-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import { v4 as uuidv4 } from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {
  private shoppingCartSubject = new BehaviorSubject<ShoppingCart | undefined>(undefined);
  public $shoppingCart = this.shoppingCartSubject.asObservable();
  private http = inject(HttpClient)
  private snackBarService = inject(SnackbarService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);

  getShoppingCart() {
    if (this.authService && this.authService.isLoggedIn()) {
      this.loadShoppingCartFromDB();
    } else {
      this.loadShoppingCartFromLocalStorage();
    }
  }

  loadShoppingCartFromDB() {
    this.http.get<ShoppingCart>('/api/shopping-carts/my-cart').subscribe({
      next: cart => {
        const shoppingCart = new ShoppingCart(
          cart.id,
          cart.cartItems,
          cart.createdAt,
          cart.createdBy,
          cart.lastModifiedAt,
          cart.lastModifiedBy
        );
        this.shoppingCartSubject.next(shoppingCart)
      },
      error: err => {
        if (err.status !== 401 && err.errors) {
          err.errors.forEach((customError: CustomError) => {
            this.snackBarService.show(customError.message, "Close")
          })
        }
      }
    });
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
      const newCart = this.createEmptyShoppingCart();
      this.shoppingCartSubject.next(newCart);
      this.saveShoppingCartToLocalStorage(newCart);
      return newCart;
    }
  }

  decrementCartItem(body: {
    cartId: string,
    isbn: string,
    inventory: number,
    shoppingCart: ShoppingCart,
  }) {
    const req = {
      cartId: body.cartId,
      isbn: body.isbn,
      quantity: -1,
      inventory: body.inventory
    };
    if (body.shoppingCart.getQuantity(body.isbn) - 1 == 0) {
      const dialogRef = this.dialog.open(QuestionDialogComponent, {
        data: {
          title: 'Delete cart item',
          message: 'Do you want to delete this cart item ?'
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) this.updateCart(req)
      })
    } else {
      this.updateCart(req)
    }
  }

  incrementCartItem(body: {
    cartId: string,
    isbn: string,
    inventory: number,
    shoppingCart: ShoppingCart,
  }) {
    this.updateCart({
      cartId: body.cartId,
      isbn: body.isbn,
      quantity: 1,
      inventory: body.inventory
    })
  }

  deleteAllCartItem(body: {
    cartId: string,
    isbn: string[],
  }) {
    if (this.authService && this.authService.isLoggedIn()) {
      this.http.post<ShoppingCart>(`/api/shopping-carts/${body.cartId}/delete-all-cart-item`, {
        isbn: body.isbn
      }).subscribe({
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
          if (err && err.error.errors) {
            err.error.errors.forEach((customError: CustomError) => {
              this.snackBarService.show(customError.message, "Close")
            })
          }
        }
      })
    } else {
      this.deleteAllCartItemLocal(body);
    }
  }

  private deleteAllCartItemLocal(body: { cartId: string; isbn: string[] }) {
    const shoppingCartLocal = this.loadShoppingCartFromLocalStorage();
    body.isbn.forEach(i => {
      const itemIndex = shoppingCartLocal.cartItems.findIndex((item: CartItem) => item.isbn === i);
      if (itemIndex !== -1) {
        shoppingCartLocal.cartItems.splice(itemIndex, 1);
      } else {
        this.snackBarService.show("Cart item local with isbn " + body.isbn + " not found!", "Close");
      }
    })

    shoppingCartLocal.lastModifiedAt = new Date().toISOString();
    this.shoppingCartSubject.next(new ShoppingCart(
      shoppingCartLocal.id,
      shoppingCartLocal.cartItems,
      shoppingCartLocal.createdAt,
      shoppingCartLocal.createdBy,
      shoppingCartLocal.lastModifiedAt,
      shoppingCartLocal.lastModifiedBy
    ));
    this.snackBarService.show("Delete cart item success", "Close")
    this.saveShoppingCartToLocalStorage(shoppingCartLocal);
  }

  deleteCartItem(body: {
    cartId: string,
    isbn: string,
  }) {
    if (this.authService && this.authService.isLoggedIn()) {
      this.http.delete<ShoppingCart>(`/api/shopping-carts/${body.cartId}/delete-cart-item`, {
        params: {isbn: body.isbn}
      }).subscribe({
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
          if (err && err.error.errors) {
            err.error.errors.forEach((customError: CustomError) => {
              this.snackBarService.show(customError.message, "Close")
            })
          }
        }
      })
    } else {
      this.deleteCartItemLocal(body);
    }
  }

  private deleteCartItemLocal(body: {
    cartId: string,
    isbn: string,
  }) {
    const shoppingCartLocal = this.loadShoppingCartFromLocalStorage();
    const i = shoppingCartLocal.cartItems.findIndex((item: CartItem) => item.isbn === body.isbn);
    if (i !== -1) {
      shoppingCartLocal.cartItems.splice(i, 1);
      this.snackBarService.show("Delete cart item success", "Close")
    } else {
      this.snackBarService.show("Cart item local with isbn " + body.isbn + " not found!", "Close");
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

  updateCart(body: {
    cartId: string,
    isbn: string,
    quantity: number,
    inventory: number
  }): void {
    if (this.authService && this.authService.isLoggedIn()) {
      this.http.post<ShoppingCart>(`/api/shopping-carts/${body.cartId}/add-to-cart`, body)
        .subscribe({
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
            if (err.status === 400 && err.error.errors) {
              err.error.errors.forEach((customError: CustomError) => {
                this.snackBarService.show(customError.message, "Close")
              })
            } else {
              this.snackBarService.show(err.message, "Close")
            }
          }
        })
    } else {
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
      else
        shoppingCartLocal.cartItems[i].quantity = quantityChanged;
    } else {
      const cartItemNew: CartItem = {
        cartId: shoppingCartLocal.id,
        id: 0,
        isbn: body.isbn,
        quantity: body.quantity,
        totalPrice: 0,
        photo: undefined,
        price: 0,
        title: undefined,
        inventory: 0,
      }
      shoppingCartLocal.cartItems.push(cartItemNew)
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

  saveShoppingCartToLocalStorage(cart: ShoppingCart) {
    localStorage.setItem('shoppingCart', JSON.stringify(cart));
  }

  createEmptyShoppingCart() {
    const usernameUUID = uuidv4()
    const cartLocalId = uuidv4()

    const newCart = new ShoppingCart(
      cartLocalId,
      [],
      new Date().toISOString(),
      usernameUUID,
      new Date().toISOString(),
      usernameUUID
    );
    return newCart;
  }

}
