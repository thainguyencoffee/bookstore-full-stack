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

  getVNPayUrl(orderId: string): Observable<any> {
    return this.http.post<string>(`/api/orders/${orderId}/payment`, {})
  }

  getOrderByOrderId(orderId: string): Observable<Order> {
    return this.http.get<Order>(`/api/orders/${orderId}`);
  }

  sendOTP(orderId: string) {
    return this.http.get(`/api/orders/${orderId}/send-opt`);
  }

  verifyOTP(orderId: string, otp: string): Observable<Order> {
    return this.http.post<Order>(`/api/orders/${orderId}/verify-otp`, {otp: otp});
  }

  updateOrder(orderDetail: Order) {
    return this.http.patch<Order>(`/api/orders/${orderDetail.id}`, orderDetail)
  }

  removeOrdersFromLocalStorage() {
    localStorage.setItem("orders", "[]")
  }
}
