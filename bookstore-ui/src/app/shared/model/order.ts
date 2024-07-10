import {UserInformation} from "./user-information";
import {Book} from "./book";

export interface LineItem {
  id: number,
  isbn: string,
  quantity: number,
  price: number,
  totalPrice: number,
  bookDetail?: Book
}

export interface Order {
  id: string,
  totalPrice: number,
  status: string,
  paymentMethod: string,
  lineItems: LineItem[],
  userInformation: UserInformation,
  createdDate: string,
  createdBy: string,
  lastModifiedDate: string,
  lastModifiedBy: string,
}
