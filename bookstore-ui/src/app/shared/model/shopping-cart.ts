export interface CartItem {
  id: number,
  cartId: string,
  isbn: string,
  title?: string,
  price?: number,
  quantity: number,
  totalPrice?: number,
  photo?: string | undefined,
  inventory: number
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

  getPrice(isbn: string): number {
    for (let item of this.cartItems) {
      if (item.isbn === isbn) return item.price ?? 0;
    }
    return 0;
  }

  hasItem(isbn: string): CartItem | undefined {
    for (let item of this.cartItems) {
      if (item.isbn === isbn) return item;
    }
    return undefined;
  }
}
