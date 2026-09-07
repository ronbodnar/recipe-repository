import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Input } from '@angular/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { MediaService } from '@core/services/media.service';
import { ButtonColor, ButtonComponent } from '@shared/ui/button/button.component';

export interface RowAction {
  label?: string;
  icon?: string;
  iconClasses?: string | string[];
  buttonColor?: ButtonColor;
  tooltipLabel?: string;
  onClick: (event: MouseEvent, data: any) => void;
  disabledFn?: (data?: any) => boolean;
  visibleFn?: (data?: any) => boolean;
}

export const EDIT: RowAction = {
  label: 'ui.tables.actions.edit',
  icon: 'edit',
  buttonColor: 'light',
  iconClasses: 'text-text!',
  tooltipLabel: 'ui.tables.actions.edit',
  onClick: () => {},
};

export const DELETE: RowAction = {
  label: 'ui.tables.actions.delete',
  icon: 'delete',
  iconClasses: 'text-danger!',
  buttonColor: 'danger',
  tooltipLabel: 'ui.tables.actions.delete',
  onClick: () => {},
};

export const DETAILS: RowAction = {
  label: 'ui.tables.actions.details',
  icon: 'info_outline',
  buttonColor: 'light',
  tooltipLabel: 'ui.tables.actions.details',
  onClick: () => {},
};

@Component({
  selector: 'app-table-row-actions',
  imports: [
    CommonModule,
    ButtonComponent,
    MatTooltipModule,
    MatMenuModule,
    MatIconModule,
    TranslatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './table-row-actions.component.html',
  styleUrl: './table-row-actions.component.css',
})
export class TableRowActionsComponent {
  @Input() rowData: any;
  @Input() actions!: RowAction[];
  @Input() expandActions = false;
  @Input() singleButton = false;

  private mediaService = inject(MediaService);

  isSmallScreen = this.mediaService.isSmallScreen;

  visibleActions = computed(() => {
    const visibleActions = this.actions.filter(
      (action) => action.visibleFn === undefined || action.visibleFn(this.rowData),
    );
    return visibleActions || [];
  });

  isVisible(action: RowAction): boolean {
    return typeof action.disabledFn === 'function' ? !action.disabledFn(this.rowData) : true;
  }

  hasActions = computed(() => this.visibleActions().length > 0);
}
