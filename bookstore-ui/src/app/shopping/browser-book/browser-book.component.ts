import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
import {Book} from "../../shared/model/book";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {distinctUntilChanged, map, Observable} from "rxjs";
import {BrowserBookDataSource} from "./browser-book-datasource";
import {MatGridListModule} from "@angular/material/grid-list";
import {AsyncPipe, CommonModule} from "@angular/common";
import {MatProgressBar} from "@angular/material/progress-bar";
import {BookCardComponent} from "../../shared/component/book-card/book-card.component";
import {ShoppingCart} from "../../shared/model/shopping-cart";
import {ShoppingCartService} from "../../shared/service/shopping-cart.service";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatSelectModule} from "@angular/material/select";
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

interface Catalog {
  name: string;
  queryName: string;
}

@Component({
  selector: 'app-browser-book',
  standalone: true,
  imports: [
    CommonModule,
    MatPaginatorModule,
    MatGridListModule,
    AsyncPipe,
    MatProgressBar,
    BookCardComponent,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    ReactiveFormsModule,
    MatIconButton,
    MatIcon,
    FormsModule,
  ],
  templateUrl: './browser-book.component.html',
  styleUrl: './browser-book.component.css'
})
export class BrowserBookComponent implements AfterViewInit, OnInit {
  private breakpointObserver = inject(BreakpointObserver);
  private shoppingCartService = inject(ShoppingCartService);
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  dataSource = new BrowserBookDataSource();
  books$: Observable<Book[]> | undefined;
  shoppingCart$: Observable<ShoppingCart | undefined> = this.shoppingCartService.$shoppingCart;
  catalogFilterQuerySubject = this.dataSource.catalogFilterQuerySubject;

  catalogControl = new FormControl<Catalog>({name: 'Default', queryName: ''});
  catalogs: Catalog[] = [
    {name: 'Default', queryName: ''},
    {name: 'Low price', queryName: '?sort=price,asc'},
    {name: 'High price', queryName: '?sort=price,desc'},
    {name: 'Best sellers', queryName: '/best-sellers'},
    {name: 'Latest release', queryName: '?sort=createdAt,desc'},
  ]
  searchByControl = new FormControl<string>('title');

  ngOnInit(): void {
    this.shoppingCartService.getShoppingCart();
  }

  onSelectChange() {
    this.catalogFilterQuerySubject.next(this.catalogControl.value?.queryName ?? '')
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.books$ = this.dataSource.connect()
  }

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

  colNumberFilter = this.breakpointObserver
    .observe([Breakpoints.XSmall, Breakpoints.Small])
    .pipe(
      map(result => {
        if (result.matches) {
          return 1;
        } else {
          return 2;
        }
      }),
      distinctUntilChanged()
    );

  searchValue: any;

  onSearch() {
    if (this.searchValue.trim()) {
      this.catalogFilterQuerySubject.next(`/search?query=${this.searchValue}&type=${this.searchByControl.value}`)
    }
  }

  onClearSearch() {
    this.searchByControl.reset('title');
    this.searchValue = '';
    this.catalogFilterQuerySubject.next('')
  }
}
