import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Order} from "../model/order";

@Injectable({
  providedIn: 'root'
})
export class EmailService {

  private http = inject(HttpClient);

  sendOTP(orderId: string) {
    return this.http.get(`/api/email/orders/${orderId}/send-opt`);
  }

  verifyOTP(orderId: string, otp: string): Observable<Order> {
    return this.http.post<Order>(`/api/email/orders/${orderId}/verify-otp`, {otp: otp});
  }

}
