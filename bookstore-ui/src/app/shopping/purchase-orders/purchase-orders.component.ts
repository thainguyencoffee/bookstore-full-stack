import {AfterViewInit, Component, inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {PurchaseOrderService} from "../../shared/service/purchase-order.service";
import {Order} from "../../shared/model/order";
import {MatList, MatListItem} from "@angular/material/list";
import {CommonModule} from "@angular/common";
import {combineLatest, interval, Observable, of, Subject, switchMap, take, takeUntil} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort, MatSortHeader} from "@angular/material/sort";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {CartItem} from "../../shared/model/shopping-cart";
import {CustomError} from "../../shared/model/api-error";
import {BookService} from "../../shared/service/book.service";
import {SnackbarService} from "../../shared/service/snackbar.service";
import {MatCardAvatar} from "@angular/material/card";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-purchase-orders',
  standalone: true,
  imports: [
    CommonModule,
    MatList,
    MatListItem,
    MatPaginator,
    MatSort,
    MatTable,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatIcon,
    MatIconButton,
    MatSortHeader,
    CdkCopyToClipboard,
    MatTooltip,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MatCardAvatar,
    RouterLink
  ],
  templateUrl: './purchase-orders.component.html',
  styleUrl: './purchase-orders.component.css'
})
export class PurchaseOrdersComponent implements OnInit, AfterViewInit, OnDestroy{
  private destroy$ = new Subject<void>();
  orders: Order[] = [];
  private bookService = inject(BookService);
  private orderService = inject(PurchaseOrderService);
  private snackbarService = inject(SnackbarService)

  @ViewChild(MatPaginator) paginator !: MatPaginator;
  @ViewChild(MatSort) sort !: MatSort;
  dataSource !: MatTableDataSource<Order>;
  displayedColumns = ['id', 'totalPrice', 'paymentMethod', 'title', 'photo', 'status', 'createdDate', 'phoneNumber', 'email', 'detail'];

  constructor() {
    this.dataSource = new MatTableDataSource<Order>([]);
  }

  ngOnInit(): void {
    this.orderService.getOrders().subscribe({
      next: page => {
        this.orders = page.content;
        this.dataSource.data = [...this.orders]
        this.getDetailOrder(this.orders)
      }
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  getDetailOrder(orders: Order[]) {
    for (let order of orders) {
      of(order).pipe(
          switchMap((order: Order) => {
            var bookDetailObservables = order.lineItems.map(lineItem => this.bookService.getBookByIsbn(lineItem.isbn));
            return bookDetailObservables.length > 0
              ? combineLatest(bookDetailObservables)
              : of([]);
          }),
          takeUntil(this.destroy$)
        )
        .subscribe({
          next: bookDetails => {
            order.lineItems.forEach((lineItem, index) => {
              lineItem.bookDetail = bookDetails[index];
            });
          },
          error: err => {
            if (err && err.errors)
              err.errors.forEach((error: CustomError) => this.snackbarService.show(error.message, "Close"));
          }
        })
    }
  }

  applyFilter(event: KeyboardEvent) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  onCopyIsbn(event: MouseEvent) {
    event.stopPropagation();
    if (this.copiedContent) {return;}
    this.copiedContent = true;
    this.startCountdown()
  }

  timeRemaining = 0;
  copiedContent = false;
  countdownSubscription: any;

  startCountdown() {
    this.timeRemaining = 5;
    this.countdownSubscription = interval(1000)
      .pipe(take(5))
      .subscribe(() => {
        this.timeRemaining--;
        if (this.timeRemaining === 0) {
          this.copiedContent = false;
          this.countdownSubscription.unsubscribe();
        }
      });
  }

}
