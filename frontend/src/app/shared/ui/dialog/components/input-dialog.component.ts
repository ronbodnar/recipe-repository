import { Component, Input, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import {
  MatInputNumberComponent,
  MatInputTextareaComponent,
  MatInputTextComponent,
} from '@ng-modular-forms/material';
import { TranslateService } from '@ngx-translate/core';
import { ButtonComponent } from '@shared/ui/button/button.component';
import { DialogComponent } from '../dialog.component';

@Component({
  selector: 'app-input-dialog',
  template: `
    <form [formGroup]="formGroup" (ngSubmit)="submit()">
      <div class="flex flex-col text-start">
        @if (message) {
          <p class="text-sm text-text my-0 mb-5" [innerHTML]="message"></p>
        }

        <div class="flex gap-4">
          @switch (type) {
            @case ('text')
            @case ('email')
            @case ('password') {
              <nmf-mat-text
                [type]="type"
                class="flex-1"
                formControlName="control"
                (keydown.enter)="$event.preventDefault()"
              />
            }
            @case ('number') {
              <nmf-mat-number
                class="flex-1"
                formControlName="control"
                (keydown.enter)="$event.preventDefault()"
              />
            }

            @case ('textarea') {
              <nmf-mat-textarea
                class="flex-1"
                [rows]="rows"
                formControlName="control"
                (keydown.enter)="$event.preventDefault()"
              />
            }
          }

          <app-button
            type="submit"
            class="mb-5.5 self-end"
            [label]="submitButtonLabel"
            [disabled]="formGroup.get('control')?.value?.length === 0"
          />
        </div>
      </div>
    </form>
  `,
  imports: [
    ReactiveFormsModule,
    MatInputNumberComponent,
    MatInputTextareaComponent,
    MatInputTextComponent,
    ButtonComponent,
  ],
})
export class InputDialogComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private translateService = inject(TranslateService);

  private readonly dialogRef = inject<MatDialogRef<DialogComponent>>(MatDialogRef);

  formGroup = this.formBuilder.group({
    control: [''],
  });

  @Input() rows = 1;
  @Input() message!: string;
  @Input() type: 'text' | 'textarea' | 'number' | 'email' | 'password' = 'text';
  @Input() submitButtonLabel!: string;

  ngOnInit(): void {
    if (this.message) {
      this.message = this.translateService.instant(this.message);
    }
  }

  submit() {
    this.dialogRef.close(this.formGroup.get('control')?.value || '');
  }
}
