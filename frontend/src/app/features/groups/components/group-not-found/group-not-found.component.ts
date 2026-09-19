import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-group-not-found',
  imports: [MatIconModule, TranslatePipe],
  templateUrl: './group-not-found.component.html',
})
export class GroupNotFoundComponent {}
