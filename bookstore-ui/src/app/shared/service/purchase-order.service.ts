import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {OrderRequest} from "../model/order-request";
import {Order} from "../model/order";
import {Observable} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class PurchaseOrderService {

  private http = inject(HttpClient)

  submitOrder(orderRequest: OrderRequest): Observable<Order> {
    return this.http.post<Order>('/api/orders', orderRequest);
  }

  getVNPayUrl(orderId: string): Observable<any> {
    return this.http.post<string>(`/api/orders/${orderId}/payment`, {})
  }

  getOrderByOrderId(orderId: string): Observable<Order> {
    return this.http.get<Order>(`/api/orders/${orderId}`);
  }
}
