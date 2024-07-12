import { CanActivateFn } from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "./shared/service/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  if (authService.isLoggedIn()) {
    return false;
  }
  return true;
};
