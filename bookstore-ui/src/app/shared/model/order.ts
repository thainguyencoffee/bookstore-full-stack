import {UserInformation} from "./user-information";

export interface Order {
  id: string,
  totalPrice: number,
  status: string,
  lineItems: [
    {
      id: number,
      isbn: string,
      quantity: number,
      price: number,
      totalPrice: number,
    }
  ],
  userInformation: UserInformation,
  createdDate: string,
  createdBy: string,
  lastModifiedDate: string,
  lastModifiedBy: string,
}
