import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthenticationService } from '../../services/authentication.service';

declare var bootstrap: any;

@Component({
  selector: 'app-top-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './top-navbar.component.html',
  styleUrl: './top-navbar.component.css',
})
export class TopNavbarComponent {

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
  }

  /*
   * After the view is initialized, add event listeners to all collapsible fields to:
   *    - Toggle chevrons when a collapse div is opened/closed
   *    - Close all other collapsible divs when a click is registered
   */
  ngAfterViewInit(): void {
    let collapseDivs = document.querySelectorAll('.collapse:not(.navbar-collapse)');

    collapseDivs.forEach((collapse) => {
      collapse.addEventListener('hide.bs.collapse', (event: any) => {
        // Get the id for the collapse target
        let targetId = event.target.id;

        // replace the keywords
        let chevronId = targetId.replace('Collapse', 'Chevron');

        // find the proper chevron element
        let chevron = document.querySelector('#' + chevronId);

        // toggle the chevron icon
        chevron?.classList.toggle('rotate-chevron');
      });
      collapse.addEventListener('show.bs.collapse', (event: any) => {
        // Get the id for the collapse target
        let targetId = event.target.id;

        // replace the keywords
        let chevronId = targetId.replace('Collapse', 'Chevron');

        // find the proper chevron element
        let chevron = document.querySelector('#' + chevronId);

        // toggle the chevron icon
        chevron?.classList.toggle('rotate-chevron');
      });
    });

    // Add a click listener that closes all other collapse elements, if the click is outside of a dropdown.
    document.addEventListener('click', (event: any) => {
      let parent = event.target.parentElement;
      collapseDivs.forEach((collapse: any) => {
        // Skip dropdown elements
        if (parent.classList.contains('dropdown') || parent.classList.contains('dropdown-menu')) return;

        // Get the Collapse instance from the element and hide it
        var bsCollapse = bootstrap.Collapse.getInstance(collapse);
        bsCollapse?.hide();
      });
    });
  }

  authenticated() {
    return this.authenticationService.isAuthenticated();
  }

  getAuthenticatedUser() {
    return this.authenticationService.getAuthenticatedUser();
  }

  deauthenticate() {
    this.authenticationService.deauthenticate().subscribe(() => this.router.navigateByUrl('/'))
  }

  getRouter(): Router {
    return this.router;
  }
}
