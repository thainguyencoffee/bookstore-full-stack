import {Component, EventEmitter, Inject, inject, Input, Output} from '@angular/core';
import {MatCardModule} from "@angular/material/card";
import {MatInputModule} from "@angular/material/input";
import {UserInformation} from "../../model/user-information";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {cities} from "../../model/cities";
import {MatOption} from "@angular/material/core";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";
import {MatSelect} from "@angular/material/select";
import {MatButtonModule} from "@angular/material/button";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {PurchaseOrderService} from "../../service/purchase-order.service";
import {Order} from "../../model/order";
import {SnackbarService} from "../../service/snackbar.service";
import {CustomError} from "../../model/api-error";

@Component({
  selector: 'app-user-information-form',
  standalone: true,
  imports: [
    MatCardModule,
    MatInputModule,
    FormsModule,
    MatOption,
    MatRadioButton,
    MatRadioGroup,
    MatSelect,
    ReactiveFormsModule,
    MatButtonModule
  ],
  templateUrl: './user-information-form-dialog.component.html',
  styleUrl: './user-information-form-dialog.component.css'
})
export class UserInformationFormDialog {

  private orderService = inject(PurchaseOrderService)
  private snackBarService = inject(SnackbarService)

  orderDetail: Order | undefined;

  constructor(
    public dialogRef: MatDialogRef<UserInformationFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Order,
  ) {
    this.orderDetail = data;
  }

  onButtonClicked(): void {
    const address = this.userInfoForm.value.address ?? '';
    const city = this.userInfoForm.value.city ?? '';
    const zipCode = this.userInfoForm.value.zipCode ?? '';

    if (this.userInfoForm
      && (address.length > 0 || city.length > 0 || zipCode.length > 0)
      && this.orderDetail
    ) {
      this.orderDetail.userInformation = {
        address: address.length > 0 ? address : this.orderDetail.userInformation.address,
        city: city.length > 0 ? city : this.orderDetail.userInformation.city,
        zipCode: zipCode.length > 0 ? zipCode : this.orderDetail.userInformation.zipCode,
        fullName: this.orderDetail.userInformation.fullName,
        email: this.orderDetail.userInformation.email,
        phoneNumber: this.orderDetail.userInformation.phoneNumber,
      }
      this.orderService.updateOrder(this.orderDetail).subscribe({
        next: () => {
          this.snackBarService.show("Update Order successfully")
          this.dialogRef.close()
        },
        error: err => {
          if (err && err.errors) {
            err.errors.forEach((err: CustomError) => this.snackBarService.show(err.message))
          }
        }
      })
    } else {
      this.snackBarService.show("Cancel update success!")
      this.dialogRef.close()
    }
  }

  userInfoForm = new FormGroup({
    address: new FormControl(''),
    city: new FormControl(''),
    zipCode: new FormControl('', Validators.pattern(/^\d{5}(?:[-\s]\d{4})?$/))
  })

  compareFn(c1: any, c2: any): boolean {
    return c1 && c2 ? c1.name === c2.name : c1 === c2;
  }

  protected readonly cities = cities;

  onCancel() {
    this.dialogRef.close()
  }
}
