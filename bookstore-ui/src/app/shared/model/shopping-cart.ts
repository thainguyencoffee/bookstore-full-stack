import {Book} from "./book";

export interface CartItem {
  id: number,
  cartId: string,
  isbn: string,
  quantity: number,
  bookDetail: Book | undefined
}

export class ShoppingCart {
  id: string;
  cartItems: CartItem[];
  createdAt: string;
  createdBy: string;
  lastModifiedAt: string;
  lastModifiedBy: string;


  constructor(id: string,
              cartItems: CartItem[],
              createdAt: string,
              createdBy: string,
              lastModifiedAt: string,
              lastModifiedBy: string) {
    this.id = id;
    this.cartItems = cartItems;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.lastModifiedAt = lastModifiedAt;
    this.lastModifiedBy = lastModifiedBy;
  }

  getQuantity(isbn: string): number {
    for (let item of this.cartItems) {
      if (item.isbn === isbn) return item.quantity;
    }
    return 0;
  }
}
