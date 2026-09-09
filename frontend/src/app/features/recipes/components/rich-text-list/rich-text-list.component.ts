import { CommonModule } from '@angular/common';
import { Component, computed, input, signal } from '@angular/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-rich-text-list',
  imports: [CommonModule, MatCheckboxModule, MatIconModule, TranslatePipe],
  templateUrl: './rich-text-list.component.html',
  styleUrl: './rich-text-list.component.css',
})
export class RichTextListComponent {
  headerText = input.required<string>();
  headerIcon = input<string | null>(null);
  emptyListText = input<string | null>(null);
  listClassList = input<string | string[] | null>(null);
  listItems = input.required<string[]>();
  ordered = input<boolean>(false);
  useCheckboxes = input<boolean>(false);

  private _lineThroughStates = signal<Map<number, boolean>>(new Map());

  public readonly lineThroughStates = this._lineThroughStates.asReadonly();

  listClasses = computed(() => {
    const classList = this.listClassList() ?? [];
    if (Array.isArray(classList)) {
      return classList.join(' ');
    }
    return classList;
  });

  listGroups = computed(() => {
    const groups: { name: string; items: string[] }[] = [];
    let current = { name: 'base', items: [] as string[] };

    groups.push(current);

    for (const item of this.listItems() ?? []) {
      if (item.startsWith('##')) {
        current = { name: item.replace('##', '').trim(), items: [] };
        groups.push(current);
        continue;
      }

      if (item.length > 0) {
        current.items.push(item);
      }
    }

    if (groups.length > 0 && groups[0].name === 'base' && groups[0].items.length === 0) {
      groups.shift();
    }

    return groups;
  });

  parseText(text: string): string {
    if (!text) {
      return '';
    }

    return text
      .replace(/##(.+)/g, '<strong>$1</strong>')
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.+?)\*/g, '<em>$1</em>')
      .replace(/_(.+?)_/g, '<u>$1</u>')
      .replace(/--/g, '&mdash;')
      .replace(/\r?\\/g, '<br>');
  }

  toggleLineThrough(index: number): void {
    this._lineThroughStates.update((states) => {
      const newStates = new Map(states);
      const currentState = newStates.get(index) || false;
      newStates.set(index, !currentState);
      return newStates;
    });
  }
}
