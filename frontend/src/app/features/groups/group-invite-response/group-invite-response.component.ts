import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { logError } from '@shared/utils/logging';
import { GroupService } from '../group.service';

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

  readonly failed = signal(false);

  constructor() {
    const groupId = this.route.snapshot.paramMap.get('id');
    const inviteCode = this.route.snapshot.paramMap.get('inviteCode');
    const response = this.route.snapshot.queryParamMap.get('response');

    console.log('groupID', groupId);
    console.log('invite code', inviteCode);
    console.log('Response', response);

    if (!groupId || (response !== 'accept' && response !== 'decline')) {
      this.failed.set(true);
      return;
    }

    const accept = response === 'accept';

    this.groupService.respondToInvite(groupId, accept).subscribe({
      next: () => {
        if (accept) {
          this.router.navigate(['/app/groups/details', groupId], { replaceUrl: true });
        } else {
          this.router.navigate(['/'], { replaceUrl: true });
        }
      },
      error: (err) => {
        logError('Failed to respond to group invite:', err);
        this.failed.set(true);
      },
    });
  }
}
