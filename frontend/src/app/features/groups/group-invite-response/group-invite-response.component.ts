import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { logError } from '@shared/utils/logging';
import { GroupService } from '../group.service';
import { ApiError } from '@core/models/api-error.model';
import { ErrorCode } from '@core/interfaces/error-code.interface';

@Component({
  selector: 'app-group-invite-response',
  imports: [TranslatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './group-invite-response.component.html',
  styleUrl: './group-invite-response.component.css',
})
export class GroupInviteResponseComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly groupService = inject(GroupService);

  readonly errorMessage = signal('');

  constructor() {
    const groupId = Number(this.route.snapshot.paramMap.get('id'));
    const inviteCode = this.route.snapshot.paramMap.get('inviteCode');
    const response = this.route.snapshot.queryParamMap.get('response');

    if (!groupId || !inviteCode || (response !== 'accept' && response !== 'decline')) {
      this.errorMessage.set('error');
      return;
    }

    const accept = response === 'accept';

    this.groupService.respondToInvitation(groupId, inviteCode, accept).subscribe({
      next: () => {
        if (accept) {
          this.router.navigate(['/app/groups/details', groupId], { replaceUrl: true });
        } else {
          this.router.navigate(['/'], { replaceUrl: true });
        }
      },
      error: (err: ApiError) => {
        logError('Failed to respond to group invite:', err);
        if (err.code === ErrorCode.NOT_INVITE_CODE_USER) {
          this.errorMessage.set('notInviteCodeUser');
        } else {
          this.errorMessage.set('error');
        }
      },
    });
  }
}
