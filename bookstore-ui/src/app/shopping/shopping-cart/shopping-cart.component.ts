import {AfterViewInit, Component, inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatPaginatorModule, MatPaginator} from '@angular/material/paginator';
import {MatSortModule, MatSort} from '@angular/material/sort';
import {MatCardAvatar} from "@angular/material/card";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {BookService} from "../../shared/service/book.service";
import {CartItem, ShoppingCart} from "../../shared/model/shopping-cart";
import {combineLatest, of, Subject, switchMap, takeUntil} from "rxjs";
import {SnackbarService} from "../../shared/service/snackbar.service";
import {CustomError} from "../../shared/model/api-error";
import {QuestionDialogComponent} from "../../shared/component/dialog/question-dialog/question-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {CommonModule} from "@angular/common";
import {AppComponent} from "../../app.component";

@Component({
  selector: 'app-data-table',
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.css',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatCardAvatar,
    MatSidenavModule,
    MatToolbarModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ]
})
export class ShoppingCartComponent implements AfterViewInit, OnDestroy, OnInit {
  private destroy$ = new Subject<void>();
  private shoppingCartService = inject(ShoppingCartService);
  private bookService = inject(BookService);
  private snackbarService = inject(SnackbarService)
  shoppingCart: ShoppingCart | undefined;
  public appComponent = inject(AppComponent)

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  dataSource!: MatTableDataSource<CartItem>;
  displayedColumns = ['isbn', 'photo', 'title', 'quantity', 'price', 'totalPrice', 'action_delete', 'action_checkout'];
  clickedRows = new Set<CartItem>();

  private dialog = inject(MatDialog);

  constructor() {
    this.dataSource = new MatTableDataSource<CartItem>([]);
  }

  ngOnInit(): void {
    this.shoppingCartService.$shoppingCart
      .pipe(
        switchMap(cart => {
          this.shoppingCart = cart;
          const bookDetailObservables = cart!.cartItems.map(item => this.bookService.getBookByIsbn(item.isbn));
          return bookDetailObservables.length > 0
            ? combineLatest(bookDetailObservables)
            : of([]);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe({
          next: bookDetails => {
            if (this.shoppingCart) {
              this.shoppingCart.cartItems.forEach((item, index) => {
                item.title = bookDetails[index].title + ' ' + bookDetails[index].author;
                item.price = bookDetails[index].price;
                item.totalPrice = bookDetails[index].price * item.quantity
                item.photo = bookDetails[index].photos?.[0]
                item.inventory = bookDetails[index].inventory;
              });
              this.dataSource = new MatTableDataSource(this.shoppingCart.cartItems);
            }
          },
          error: err => {
            if (err && err.errors) {
              err.errors.forEach((err: CustomError) => this.snackbarService.show(err.message))
            }
          }
        }
      );
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onRowClick(row: CartItem) {
    this.clickedRows.has(row) ? this.clickedRows.delete(row) : this.clickedRows.add(row);
  }

  removeSelected() {
    let isbnArray: string[] = []
    this.clickedRows.forEach(row => {
      isbnArray.push(row.isbn)
      this.clickedRows.delete(row)
    })
    if (this.shoppingCart) {
      this.shoppingCartService.deleteAllCartItem({
        cartId: this.shoppingCart.id,
        isbn: isbnArray
      })
    }
  }

  increaseQuantity(event: MouseEvent, row: CartItem) {
    event.stopPropagation();
    if (this.shoppingCart) {
      this.shoppingCartService.incrementCartItem({
        cartId: row.cartId,
        isbn: row.isbn,
        inventory: row.inventory,
        shoppingCart: this.shoppingCart
      });
    }
  }

  decreaseQuantity(event: MouseEvent, row: CartItem) {
    event.stopPropagation();
    if (this.shoppingCart) {
      this.shoppingCartService.decrementCartItem({
        cartId: row.cartId,
        isbn: row.isbn,
        inventory: row.inventory,
        shoppingCart: this.shoppingCart
      })
    }
  }

  getListItemClicked(): Set<CartItem> {
    return this.clickedRows;
  }

  applyFilter(event: KeyboardEvent) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  deleteCartItem(event: MouseEvent, row: CartItem) {
    event.stopPropagation();
    const dialogRef = this.dialog.open(QuestionDialogComponent, {
      data: {
        title: 'Delete cart item',
        message: 'Do you want to delete this cart item ?'
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) this.shoppingCartService.deleteCartItem({isbn: row.isbn, cartId: row.cartId})
    })
  }

  getTotalPrice(): number {
    return this.dataSource.data.reduce((total, item) => {
      return total + (item.totalPrice ?? 0)
    }, 0)
  }

}
