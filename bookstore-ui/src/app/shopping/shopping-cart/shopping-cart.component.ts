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
import {combineLatest, interval, of, Subject, switchMap, take, takeUntil} from "rxjs";
import {SnackbarService} from "../../shared/service/snackbar.service";
import {CustomError} from "../../shared/model/api-error";
import {QuestionDialogComponent} from "../../shared/component/dialog/question-dialog/question-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {CommonModule} from "@angular/common";
import {AppComponent} from "../../app.component";
import {ActivatedRoute, Router} from "@angular/router";
import {CdkCopyToClipboard} from "@angular/cdk/clipboard";
import {MatTooltip} from "@angular/material/tooltip";

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
    CdkCopyToClipboard,
    MatTooltip,
  ]
})
export class ShoppingCartComponent implements AfterViewInit, OnDestroy, OnInit {
  private destroy$ = new Subject<void>();
  private shoppingCartService = inject(ShoppingCartService);
  private bookService = inject(BookService);
  private snackbarService = inject(SnackbarService)
  shoppingCart: ShoppingCart | undefined;
  public appComponent = inject(AppComponent)
  private route = inject(ActivatedRoute)

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  dataSource!: MatTableDataSource<CartItem>;
  displayedColumns = ['isbn', 'photo', 'title', 'quantity', 'price', 'totalPrice', 'action_delete', 'action_checkout'];
  clickedRows = new Set<string>();


  private dialog = inject(MatDialog);
  private router = inject(Router);

  constructor() {
    this.dataSource = new MatTableDataSource<CartItem>([]);
  }

  ngOnInit(): void {
    this.route.params.subscribe(() => {
      this.shoppingCartService.getShoppingCart();
    });

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
                if (item.quantity > bookDetails[index].inventory) {
                  item.quantity = bookDetails[index].inventory;
                  item.totalPrice = item.quantity * item.price;
                }
              });
              this.dataSource.data = [...this.shoppingCart.cartItems]
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
    this.clickedRows.has(row.isbn) ? this.clickedRows.delete(row.isbn) : this.clickedRows.add(row.isbn);
  }

  removeSelected() {
    let isbnArray: string[] = []
    this.clickedRows.forEach(isbn => {
      isbnArray.push(isbn)
      this.clickedRows.delete(isbn)
    })
    if (this.shoppingCart) {
      this.shoppingCartService.deleteAllCartItem({
        cartId: this.shoppingCart.id,
        isbn: isbnArray
      })
    }
  }

  increaseQuantity(event: MouseEvent, row: CartItem) {
    // event.preventDefault();
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
    // event.preventDefault();
    event.stopPropagation();
    if (this.shoppingCart) {
      const req = {
        cartId: row.cartId,
        isbn: row.isbn,
        quantity: -1,
        inventory: row.inventory
      };
      if (this.shoppingCart.getQuantity(row.isbn) - 1 == 0) {
        const dialogRef = this.dialog.open(QuestionDialogComponent, {
          data: {
            title: 'Delete cart item',
            message: 'Do you want to delete this cart item ?'
          }
        });
        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            this.shoppingCartService.updateCart(req)
            this.clickedRows.delete(row.isbn)
          }
        })
      } else {
        this.shoppingCartService.updateCart(req)
      }
    }
  }

  getListItemClicked(): Set<string> {
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
    let total = 0;
    this.clickedRows.forEach(isbn => {
      if (this.shoppingCart) {
        total += this.shoppingCart.getQuantity(isbn) * this.shoppingCart.getPrice(isbn);
      }
    })
    return total
  }

  isOutOfStock(isbn: string): boolean {
    for (let item of this.dataSource.data) {
      if (item.isbn === isbn) {
        return item.inventory === 0;
      }
    }
    return false;
  }

  checkoutSelected() {
    if (this.shoppingCart) {
      const selectedRows: CartItem[] = []
      for (let isbn of this.clickedRows) {
        if (this.isOutOfStock(isbn)) {
          this.snackbarService.show('Out of stock item with isbn ' + isbn);
          return;
        }
        const cartItem = this.shoppingCart?.hasItem(isbn);
        if (cartItem) selectedRows.push(cartItem);
        else {
          this.snackbarService.show("Not existing cart item with isbn " + isbn);
          return;
        }
      }
      this.router.navigate(['/checkout'], { state: { selectedRows } });
    }
  }

  checkout(isbn: string) {
    let selectedItem: CartItem | undefined;
    if (this.shoppingCart) {
      const cartItem = this.shoppingCart?.hasItem(isbn);
      if (cartItem) selectedItem = cartItem
      else {
        this.snackbarService.show("Not existing cart item with isbn " + isbn)
        return;
      }
    }
    this.router.navigate(['/checkout'], { state: { selectedItem } });
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
