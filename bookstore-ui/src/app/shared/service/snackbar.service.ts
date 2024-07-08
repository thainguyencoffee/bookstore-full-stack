import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {
  private queue: string[] = [];
  private isShowing: boolean = false;

  constructor(private snackBar: MatSnackBar) {}

  show(message: string) {
    this.queue.push(message);
    if (!this.isShowing) {
      this.displayNext();
    }
  }

  private displayNext() {
    if (this.queue.length > 0) {
      this.isShowing = true;
      const message = this.queue.shift()!;
      this.snackBar.open(message, 'Close',
        { duration: 5000 }
      ).afterDismissed().subscribe(() => {
        this.isShowing = false;
        this.displayNext();
      });
    }
  }
}
