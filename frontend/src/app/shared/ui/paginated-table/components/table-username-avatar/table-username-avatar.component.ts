import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-table-username-avatar',
  imports: [MatIconModule],
  templateUrl: './table-username-avatar.component.html',
  styleUrl: './table-username-avatar.component.css',
})
export class TableUsernameAvatarComponent {
  @Input() username!: string;
  @Input() profileImageUrl?: string;
}
