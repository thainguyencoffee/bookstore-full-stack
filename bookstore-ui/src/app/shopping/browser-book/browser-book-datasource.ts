import {DataSource} from "@angular/cdk/collections";
import {inject} from "@angular/core";
import {MatPaginator} from "@angular/material/paginator";
import {BookService} from "../../shared/service/book.service";
import {map, Observable, merge, BehaviorSubject, startWith, switchMap, catchError, of} from "rxjs";
import {tap} from "rxjs/operators";
import {Book} from "../../shared/model/book";
import {Page} from "../../shared/model/page";

export class BrowserBookDataSource extends DataSource<Book> {

  private bookService = inject(BookService);
  public paginator: MatPaginator | undefined;

  private dataSubject = new BehaviorSubject<Book[]>([]);
  public $data = this.dataSubject.asObservable();

  private totalElementsSubject = new BehaviorSubject<number>(0);
  public totalElements$ = this.totalElementsSubject.asObservable();
  catalogFilterQuerySubject = new BehaviorSubject<string>('');
  catalogFilterQuery$ = this.catalogFilterQuerySubject.asObservable();

  constructor() {
    super();
    this.catalogFilterQuery$.subscribe(query => {
      this.currentCatalogFilterQuery = query;
      this.refreshData();
    });
  }

  private currentCatalogFilterQuery: string | null = null; // Lưu query hiện tại

  connect(): Observable<Book[]> {
    if (this.paginator) {
      return merge(this.paginator.page)
        .pipe(
          startWith({}),
          switchMap(() => {
            const params = {
              page: this.paginator!.pageIndex,
              size: this.paginator!.pageSize,
            }
            return this.loadData(params);
          }),
          map((page) => page.content),
          catchError(() => of([]))
        );
    } else {
      throw Error('Please set the paginator and sort on the data source');
    }
  }

  private loadData(params: any): Observable<Page<Book>> {
    return this.bookService.getAllBook(params, this.currentCatalogFilterQuery ?? '')
      .pipe(
        tap((page: Page<Book>) => {
          this.paginator!.length = page.totalElements;
          this.dataSubject.next(page.content);
          this.totalElementsSubject.next(page.totalElements)
        })
      );
  }

  disconnect(): void {
  }

  refreshData() {
    if (this.paginator) {
      this.paginator.pageIndex = 0; // Reset về trang đầu tiên khi filter thay đổi
      this.paginator.page.next({
        pageIndex: this.paginator.pageIndex,
        pageSize: this.paginator.pageSize,
        length: this.paginator.length
      });
    }
  }

}
