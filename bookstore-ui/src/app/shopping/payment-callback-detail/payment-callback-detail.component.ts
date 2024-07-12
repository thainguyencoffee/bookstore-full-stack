import {AfterViewInit, Component, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {ActivatedRoute, RouterLink} from "@angular/router";
import {PurchaseOrderService} from "../../shared/service/purchase-order.service";
import {Order} from "../../shared/model/order";
import {SnackbarService} from "../../shared/service/snackbar.service";
import {CustomError} from "../../shared/model/api-error";
import {MatCardModule} from "@angular/material/card";
import {MatChipsModule} from "@angular/material/chips";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {CommonModule} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatAccordion, MatExpansionModule} from "@angular/material/expansion";
import {MatListModule} from "@angular/material/list";
import {combineLatest, interval, of, Subject, switchMap, take, takeUntil} from "rxjs";
import {BookService} from "../../shared/service/book.service";
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {ShoppingCart} from "../../shared/model/shopping-cart";
import {MatError, MatFormField} from "@angular/material/form-field";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {
  UserInformationFormDialog
} from "../../shared/component/user-information-form-dialog/user-information-form-dialog.component";
import {AuthService} from "../../shared/service/auth.service";

@Component({
  selector: 'app-payment-callback-detail',
  standalone: true,
  imports: [CommonModule,
    MatCardModule,
    MatChipsModule,
    MatProgressBarModule,
    MatButtonModule,
    CdkCopyToClipboard,
    MatAccordion,
    MatExpansionModule,
    RouterLink,
    MatListModule,
    MatFormField, FormsModule, MatInput, ReactiveFormsModule, MatError, MatIcon
  ],
  templateUrl: './payment-callback-detail.component.html',
  styleUrl: './payment-callback-detail.component.css'
})
export class PaymentCallbackDetailComponent implements OnInit, OnDestroy, AfterViewInit {
  private destroy$ = new Subject<void>();
  orderDetail: Order | undefined;

  private route = inject(ActivatedRoute);
  private matDialog = inject(MatDialog)
  private orderService = inject(PurchaseOrderService);
  private shoppingCartService = inject(ShoppingCartService);
  private $shoppingCart = this.shoppingCartService.$shoppingCart;
  private shoppingCart: ShoppingCart | undefined;
  private bookService = inject(BookService)
  private snackbarService = inject(SnackbarService);
  authService = inject(AuthService);

  ngOnInit() {
    this.$shoppingCart.subscribe(shoppingCart => this.shoppingCart = shoppingCart)
    this.route.paramMap.subscribe({
      next: params => {
        const orderId = params.get('orderId') ?? undefined;
        if (orderId) {
          this.orderService.getOrderByOrderId(orderId)
            .pipe(
              switchMap((order: Order) => {
                this.orderDetail = order;
                var bookDetailObservables = order.lineItems.map(lineItem => this.bookService.getBookByIsbn(lineItem.isbn));
                return bookDetailObservables.length > 0
                  ? combineLatest(bookDetailObservables)
                  : of([]);
              }),
              takeUntil(this.destroy$)
            )
            .subscribe({
              next: bookDetails => {
                if (this.orderDetail) {
                  this.orderDetail.lineItems.forEach((lineItem, index) => {
                    lineItem.bookDetail = bookDetails[index];
                  });
                }
              },
              error: err => {
                if (err && err.errors)
                  err.errors.forEach((error: CustomError) => this.snackbarService.show(error.message, "Close"));
              }
            })
        }
      }
    })
  }

  ngAfterViewInit(): void {
    this.$shoppingCart.subscribe(shoppingCart => this.shoppingCart = shoppingCart)
    if (this.orderDetail && this.orderDetail.status == "ACCEPTED") {
      console.log("Delete all cart item")
      let isbnArray: string[] = []
      this.orderDetail.lineItems.forEach(lineItem => {
        isbnArray.push(lineItem.isbn)
      })
      if (this.shoppingCart) {
        this.shoppingCartService.deleteAllCartItem({
          cartId: this.shoppingCart.id,
          isbn: isbnArray
        })
      }
    }
  }

  copySuccess() {
    this.snackbarService.show('Order ID copied to clipboard', "Close")
  }

  onRepayment(orderId: string) {
    this.orderService.getVNPayUrl(orderId).subscribe({
      next: (payment: any) => {
        if (payment && payment.paymentUrl) {
          window.location.href = payment.paymentUrl
        }
      }
    });
  }

  sendOtp(orderId: string) {
    if (this.isSendingOtp) {
      return;
    }
    this.isSendingOtp = true;
    this.startCountdown();
    this.orderService.sendOTP(orderId).subscribe({
      next: () => {
        this.snackbarService.show('OTP sent successfully', "Close");
        if (this.timeRemaining === 0) {
          this.isSendingOtp = false;
        }
      },
      error: err => {
        if (err && err.errors) {
          err.errors.forEach((error: CustomError) => this.snackbarService.show(error.message, "Close"));
          this.isSendingOtp = false;
        }
      }
    });
  }

  verifyOtp(orderId: string) {
    if (this.otpFormGroup.valid && this.otpFormGroup.value.otp) {
      if (this.isVerifyingOtp) {
        return;
      }
      this.isVerifyingOtp = true;
      this.orderService.verifyOTP(orderId, this.otpFormGroup.value.otp).subscribe({
        next: (order: Order) => {
          this.orderDetail = order
          this.snackbarService.show('OTP verified successfully', "Close");
          this.isVerifyingOtp = false;
        },
        error: err => {
          if (err.status >= 400 && err.status < 500) {
            this.snackbarService.show('OTP is incorrect', "Close");
            this.isVerifyingOtp = false;
          }
        }
      });
    }
  }

  updateOrder() {
    this.matDialog.open(UserInformationFormDialog, {
      data: this.orderDetail
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  startCountdown() {
    this.timeRemaining = 60;
    this.countdownSubscription = interval(1000)
      .pipe(take(60))
      .subscribe(() => {
        this.timeRemaining--;
        if (this.timeRemaining === 0) {
          this.isSendingOtp = false;
          this.countdownSubscription.unsubscribe();
        }
      });
  }

  isSendingOtp = false;
  isVerifyingOtp = false;
  countdownSubscription: any;
  timeRemaining = 0;

  otpFormGroup = new FormGroup({
    otp: new FormControl('', [Validators.required, Validators.pattern(/^\d{6}$/)])
  })


}
