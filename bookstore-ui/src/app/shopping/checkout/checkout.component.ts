import {Component, inject,} from '@angular/core';
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";
import {CartItem} from "../../shared/model/shopping-cart";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";
import {cities} from "../../shared/model/cities";
import {PurchaseOrderService} from "../../shared/service/purchase-order.service";
import {OrderRequest} from "../../shared/model/order-request";
import {SnackbarService} from "../../shared/service/snackbar.service";
import {CustomError} from "../../shared/model/api-error";
import {Book} from "../../shared/model/book";
import {AuthService} from "../../shared/service/auth.service";
import {PaymentService} from "../../shared/service/payment.service";
import {catchError, combineLatest, of, switchMap} from "rxjs";

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatButtonModule,
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatRadioButton,
    MatRadioGroup
  ],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent {
  cartItemSelected: CartItem[] | undefined;
  bookItem: Book | undefined;
  private router = inject(Router);
  private orderService = inject(PurchaseOrderService)
  private paymentService = inject(PaymentService)
  private snackbarService = inject(SnackbarService)
  cities = cities
  private authService = inject(AuthService);

  constructor() {
    const navigation = this.router.getCurrentNavigation();
    const selectedRows = navigation?.extras?.state?.['selectedRows'];
    const selectedItem = navigation?.extras?.state?.['selectedItem'];
    const singleItem = navigation?.extras?.state?.['singleItem'];

    if (Array.isArray(selectedRows) && selectedRows.length > 0) {
      this.cartItemSelected = selectedRows;
    } else if (selectedItem) {
      this.cartItemSelected = [selectedItem];
    } else if (singleItem) {
      this.bookItem = singleItem;
    }
  }

  purchaseOrderFormGroup = new FormGroup({
    fullName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(09[0-9]{8}|033[0-9]{7})$/)]),
    city: new FormControl('', Validators.required),
    zipCode: new FormControl('', [Validators.required, Validators.pattern(/^\d{5}(?:[-\s]\d{4})?$/)]),
    address: new FormControl('', [Validators.required]),
    paymentMethod: new FormControl('CASH', Validators.required)
  })

  onSubmit() {
    if (this.purchaseOrderFormGroup.valid
      && this.purchaseOrderFormGroup.value.fullName
      && this.purchaseOrderFormGroup.value.email
      && this.purchaseOrderFormGroup.value.phoneNumber
      && this.purchaseOrderFormGroup.value.city
      && this.purchaseOrderFormGroup.value.zipCode
      && this.purchaseOrderFormGroup.value.address
      && this.purchaseOrderFormGroup.value.paymentMethod
    ) {
      let orderRequest: OrderRequest = {
        lineItems: [],
        userInformation: {
          fullName: this.purchaseOrderFormGroup.value.fullName,
          email: this.purchaseOrderFormGroup.value.email,
          phoneNumber: this.purchaseOrderFormGroup.value.phoneNumber,
          city: this.purchaseOrderFormGroup.value.city,
          zipCode: this.purchaseOrderFormGroup.value.zipCode,
          address: this.purchaseOrderFormGroup.value.address,
        },
        paymentMethod: this.purchaseOrderFormGroup.value.paymentMethod
      }
      if (this.cartItemSelected && this.cartItemSelected.length > 0) {
        this.cartItemSelected.forEach(item => orderRequest.lineItems.push({isbn: item.isbn, quantity: item.quantity}))
        combineLatest([
          this.orderService.submitOrder(orderRequest),
          this.authService.$authState
        ]).pipe(
          switchMap(([order, authState]) => {
            this.snackbarService.show("Create order success: orderID " + order.id, "Close");

            if (!authState.isAuthenticated) {
              this.orderService.saveOrderToLocalStorage(order);
            }

            if (order.paymentMethod === 'VNPAY') {
              return this.paymentService.getVNPayUrl(order.id).pipe(
                switchMap((payment: any) => {
                  if (payment && payment.paymentUrl) {
                    window.location.href = payment.paymentUrl;
                    return of(null); // End the observable chain
                  } else {
                    // Handle case where paymentUrl is not returned as expected
                    this.router.navigate(['/order-detail-callback', order.id]);
                    return of(null);
                  }
                })
              );
            } else {
              this.router.navigate(['/order-detail-callback', order.id]);
              return of(null); // End the observable chain
            }
          }),
          catchError(err => {
            if (err && err.errors) {
              err.errors.forEach((error: CustomError) => {
                this.snackbarService.show(error.message, "Close");
              });
            }
            return of(null); // Handle the error and end the observable chain
          })
        ).subscribe();
      } else {
        this.snackbarService.show("Not resolve the cart items", "Close")
      }
    }
  }
}
