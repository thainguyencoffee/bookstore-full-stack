import {Component, Inject, inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-question-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './question-dialog.component.html',
  styleUrl: './question-dialog.component.css'
})
export class QuestionDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data:
      { title: string, message: string }
  ) {
  }

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }

  readonly dialogRef = inject(MatDialogRef<QuestionDialogComponent>)
}
