import {Routes} from '@angular/router';
import {BrowserBookComponent} from "./shopping/browser-book/browser-book.component";
import {ShoppingCartComponent} from "./shopping/shopping-cart/shopping-cart.component";

export const routes: Routes = [
  {path: "", component: BrowserBookComponent},
  {path: "shopping-carts", component: ShoppingCartComponent}
];
