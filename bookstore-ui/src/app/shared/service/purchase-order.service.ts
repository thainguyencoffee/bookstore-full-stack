import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {OrderRequest} from "../model/order-request";
import {Order} from "../model/order";
import {Observable} from "rxjs";
import {Page} from "../model/page";


@Injectable({
  providedIn: 'root'
})
export class PurchaseOrderService {

  private http = inject(HttpClient)

  getOrders(): Observable<Page<Order>> {
    return this.http.get<Page<Order>>('/api/orders');
  }

  submitOrder(orderRequest: OrderRequest): Observable<Order> {
    return this.http.post<Order>('/api/orders', orderRequest);
  }

  saveOrderToLocalStorage(order: Order) {
    const orders = JSON.parse(localStorage.getItem('orders') || '[]');
    orders.push({id: order.id});
    localStorage.setItem('orders', JSON.stringify(orders));
  }

  getOrdersFromLocalStorage(): {id: string}[] {
    return JSON.parse(localStorage.getItem("orders") || "[]");
  }

  getOrderByOrderId(orderId: string, isGuest: boolean = false): Observable<Order> {
    if (isGuest) {
      return this.http.get<Order>(`/api/guest-orders/${orderId}`);
    }
    return this.http.get<Order>(`/api/orders/${orderId}`);
  }

  updateOrder(orderDetail: Order, isGuest: boolean = false): Observable<Order> {
    if (isGuest) {
      return this.http.patch<Order>(`/api/guest-orders/${orderDetail.id}`, orderDetail)
    } else {
      return this.http.patch<Order>(`/api/orders/${orderDetail.id}`, orderDetail)
    }
  }

  removeOrdersFromLocalStorage() {
    localStorage.setItem("orders", "[]")
  }
}
