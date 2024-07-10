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
import {combineLatest, of, Subject, switchMap, takeUntil} from "rxjs";
import {BookService} from "../../shared/service/book.service";
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {ShoppingCart} from "../../shared/model/shopping-cart";

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
    MatListModule
  ],
  templateUrl: './payment-callback-detail.component.html',
  styleUrl: './payment-callback-detail.component.css'
})
export class PaymentCallbackDetailComponent implements OnInit, OnDestroy, AfterViewInit {
  private destroy$ = new Subject<void>();
  orderDetail: Order | undefined;

  private route = inject(ActivatedRoute);
  private orderService = inject(PurchaseOrderService);
  private shoppingCartService = inject(ShoppingCartService);
  private $shoppingCart = this.shoppingCartService.$shoppingCart;
  private shoppingCart: ShoppingCart | undefined;
  private bookService = inject(BookService)
  private snackbarService = inject(SnackbarService);

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
                // handle delete cart item if order accepted
                // if (order && order.status == "ACCEPTED") {
                //   let isbnArray: string[] = []
                //   order.lineItems.forEach(lineItem => {
                //     isbnArray.push(lineItem.isbn)
                //   })
                //   if (this.shoppingCart) {
                //     this.shoppingCartService.deleteAllCartItem({
                //       cartId: this.shoppingCart.id,
                //       isbn: isbnArray
                //     })
                //   }
                // }
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
                  err.errors.forEach((error: CustomError) => this.snackbarService.show(error.message));
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
    this.snackbarService.show('Order ID copied to clipboard')
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

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
