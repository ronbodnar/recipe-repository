import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';

@Component({
  selector: 'app-checkbox-group',
  imports: [MatCheckboxModule, MatExpansionModule],
  templateUrl: './checkbox-group.component.html',
  styleUrl: './checkbox-group.component.css',
})
export class CheckboxGroupComponent {
  @Input() label = '';
  @Input() options: { value: string; label: string }[] = [];
  @Input() control!: FormControl<string[]>;
  @Input() wrapperClass = '';

  toggleValue<T>(control: FormControl<T[]>, value: T, checked: boolean): void {
    const current = control.value ?? [];

    control.setValue(checked ? [...current, value] : current.filter((v) => v !== value));

    control.markAsDirty();
  }

  isValueSelected<T>(control: FormControl<T[]>, value: T): boolean {
    const current = control.value ?? [];

    return current.includes(value);
  }
}
