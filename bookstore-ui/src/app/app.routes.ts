import {Routes} from '@angular/router';
import {BrowserBookComponent} from "./shopping/browser-book/browser-book.component";
import {ShoppingCartComponent} from "./shopping/shopping-cart/shopping-cart.component";
import {CheckoutComponent} from "./shopping/checkout/checkout.component";
import {OrderDetailComponent} from "./shopping/order-detail/order-detail.component";
import {BookDetailComponent} from "./shopping/book-detail/book-detail.component";
import {PurchaseOrdersComponent} from "./shopping/purchase-orders/purchase-orders.component";
import {authGuard} from "./auth.guard";

export const routes: Routes = [
  {path: "", component: BrowserBookComponent},
  {path: "book-detail/:isbn", component: BookDetailComponent},
  {path: "purchase-orders", component: PurchaseOrdersComponent},
  {path: "shopping-carts", component: ShoppingCartComponent},
  {path: "checkout", component: CheckoutComponent},
  {path: "order-detail-callback/:orderId", component: OrderDetailComponent},
  {path: "order-detail/:orderId", component: OrderDetailComponent, canActivate: [authGuard]},
];
