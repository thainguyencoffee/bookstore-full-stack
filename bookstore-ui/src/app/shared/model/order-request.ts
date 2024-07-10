import {UserInformation} from "./user-information";

export interface OrderRequestLineIte {
  isbn: string,
  quantity: number
}

export interface OrderRequest {
  lineItems: OrderRequestLineIte[],
  userInformation: UserInformation | undefined
}
