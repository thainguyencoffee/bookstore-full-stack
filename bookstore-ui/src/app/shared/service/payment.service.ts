import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private http = inject(HttpClient);

  getVNPayUrl(orderId: string): Observable<any> {
    return this.http.post<string>(`/api/payment/vn-pay/payment-url`, {orderId: orderId})
  }

}
