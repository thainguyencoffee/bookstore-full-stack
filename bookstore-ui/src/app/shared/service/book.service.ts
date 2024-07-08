import {inject, Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Book} from "../model/book";
import {Page} from "../model/page";

@Injectable({
  providedIn: 'root'
})
export class BookService {

  private http = inject(HttpClient);

  getAllBook(params: {
    page: number,
    size: number
  }): Observable<Page<Book>> {
    let httpParams = new HttpParams()
      .set('page', params.page)
      .set('size', params.size);
    return this.http.get<Page<Book>>('/api/books', { params: httpParams });
  }

  getBookByIsbn(isbn: string): Observable<Book> {
    return this.http.get<Book>(`/api/books/${isbn}`);
  }

}
