import {Routes} from '@angular/router';
import {BrowserBookComponent} from "./shopping/browser-book/browser-book.component";
import {ShoppingCartComponent} from "./shopping/shopping-cart/shopping-cart.component";
import {CheckoutComponent} from "./shopping/checkout/checkout.component";
import {PaymentCallbackDetailComponent} from "./shopping/payment-callback-detail/payment-callback-detail.component";
import {BookDetailComponent} from "./shopping/book-detail/book-detail.component";

export const routes: Routes = [
  {path: "", component: BrowserBookComponent},
  {path: "book-detail/:isbn", component: BookDetailComponent},
  {path: "shopping-carts", component: ShoppingCartComponent},
  {path: "checkout", component: CheckoutComponent},
  {path: "payment-callback-detail/:orderId", component: PaymentCallbackDetailComponent},
];
