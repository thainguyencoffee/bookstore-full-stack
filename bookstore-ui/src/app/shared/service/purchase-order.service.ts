import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {SnackbarService} from "./snackbar.service";
import {AuthService} from "./auth.service";
import {OrderRequest} from "../model/order-request";
import {Order} from "../model/order";
import {CustomError} from "../model/api-error";


@Injectable({
  providedIn: 'root'
})
export class PurchaseOrderService {

  private http = inject(HttpClient)
  private snackBarService = inject(SnackbarService);
  private authService = inject(AuthService);

  submitOrder(orderRequest: OrderRequest) {
    if (this.authService && this.authService.isLoggedIn()) {
      this.createOrder(orderRequest);
    }
    else {
      this.authService.login();
    }
  }

  private createOrder(orderRequest: OrderRequest) {
    this.http.post<Order>('/api/orders', orderRequest).subscribe({
      next: (order: Order) => {
        this.snackBarService.show("Create order success: orderID " + order.id);
      },
      error: err => {
        if (err && err.errors) {
          err.errors.forEach((err: CustomError) => {
            this.snackBarService.show(err.message)
          })
        }
      }
    })
  }

}
