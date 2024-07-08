import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
import {DataSource} from "@angular/cdk/collections";
import {Book} from "../../shared/model/book";
import {BookService} from "../../shared/service/book.service";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {AppComponent} from "../../app.component";
import {distinctUntilChanged, map, Observable} from "rxjs";
import {BrowserBookDataSource} from "./browser-book-datasource";
import {MatGridListModule} from "@angular/material/grid-list";
import {AsyncPipe, CommonModule} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {BookCardComponent} from "../../shared/component/book-card/book-card.component";
import {ShoppingCart} from "../../shared/model/shopping-cart";
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";

@Component({
  selector: 'app-browser-book',
  standalone: true,
  imports: [
    CommonModule,
    MatPaginatorModule,
    MatGridListModule,
    AsyncPipe,
    MatProgressBar,
    BookCardComponent
  ],
  templateUrl: './browser-book.component.html',
  styleUrl: './browser-book.component.css'
})
export class BrowserBookComponent implements AfterViewInit, OnInit{
  private breakpointObserver = inject(BreakpointObserver);
  private shoppingCartService = inject(ShoppingCartService);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  dataSource = new BrowserBookDataSource();
  books$: Observable<Book[]> | undefined;
  shoppingCart$: Observable<ShoppingCart | undefined> = this.shoppingCartService.$shoppingCart;

  colNumber = this.breakpointObserver
    .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium, Breakpoints.Large, Breakpoints.XLarge])
    .pipe(
      map(result => {
        if (result.matches) {
          if (result.breakpoints[Breakpoints.XSmall]) return 1;
          else if (result.breakpoints[Breakpoints.Small]) return 2;
          else if (result.breakpoints[Breakpoints.Medium]) return 3;
          else if (result.breakpoints[Breakpoints.Large]) return 4;
          else return 6;
        } else {
          return 6;
        }
      }),
      distinctUntilChanged()
    );

  ngOnInit(): void {
    this.shoppingCartService.getShoppingCart();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.books$ = this.dataSource.connect()
  }

}
