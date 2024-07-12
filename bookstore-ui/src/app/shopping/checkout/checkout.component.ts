import {Component, inject, } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
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
import {Order} from "../../shared/model/order";
import {CustomError} from "../../shared/model/api-error";
import {Book} from "../../shared/model/book";

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
  private snackbarService = inject(SnackbarService)
  cities = cities

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
        this.orderService.submitOrder(orderRequest).subscribe({
          next: (order: Order) => {
            this.snackbarService.show("Create order success: orderID " + order.id, "Close");
            // save order to localstorage
            this.orderService.saveOrderToLocalStorage(order);
            if (order.paymentMethod === 'VNPAY') {
              this.orderService.getVNPayUrl(order.id).subscribe({
                next: (payment: any) => {
                  if (payment && payment.paymentUrl) {
                    window.location.href = payment.paymentUrl
                  }
                }
              });
            } else {
              this.router.navigate(['/payment-callback-detail', order.id])
            }
          },
          error: err => {
            if (err && err.errors) {
              err.errors.forEach((err: CustomError) => {
                this.snackbarService.show(err.message, "Close")
              })
            }
          }
        })
      }
      else {
        this.snackbarService.show("Not resolve the cart items", "Close")
      }
    }
  }
}
