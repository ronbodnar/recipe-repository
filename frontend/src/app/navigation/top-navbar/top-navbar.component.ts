import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthenticationService } from '../../authentication.service';

@Component({
  selector: 'app-top-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './top-navbar.component.html',
  styleUrl: './top-navbar.component.css'
})
export class TopNavbarComponent {

  constructor(private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    let collapseChevrons = document.querySelectorAll('.dropdown-menu.collapse');
    collapseChevrons.forEach((collapse) => {
      collapse.addEventListener('hide.bs.collapse', (event: any) => {
        // Get the id for the collapse target
        let targetId = event.target.id

        // replace the keywords
        let chevronId = targetId.replace("Collapse", "Chevron")

        // find the proper chevron element
        let chevron = document.querySelector('#' + chevronId)

        // toggle the chevron icon
        chevron?.classList.toggle('rotate-chevron');
      });
      collapse.addEventListener('show.bs.collapse', (event: any) => {
        // Get the id for the collapse target
        let targetId = event.target.id

        // replace the keywords
        let chevronId = targetId.replace("Collapse", "Chevron")

        // find the proper chevron element
        let chevron = document.querySelector('#' + chevronId)

        // toggle the chevron icon
        chevron?.classList.toggle('rotate-chevron');
      });
    });

    /*let collapseChevrons = document.querySelectorAll('.collapse');
    collapseChevrons.forEach((collapse) => {
      collapse.addEventListener('hide.bs.collapse', (event: any) => {

        // Get the id for the collapse target
        let targetId = event.target.id

        console.log(targetId)

        // replace the keywords
        let chevronId = targetId.replace("Dropdown", "Chevron")

        console.log(chevronId)

        // find the proper chevron element
        let chevron = document.querySelector('#' + chevronId)

        console.log(chevron)

        // toggle the chevron icon
        chevron?.classList.toggle('rotate-chevron');
      });
      collapse.addEventListener('show.bs.collapse', (event: any) => {
        // Get the id for the collapse target
        let targetId = event.target.id

        console.log(targetId)

        // replace the keywords
        let chevronId = targetId.replace("Dropdown", "Chevron")

        console.log(chevronId)

        // find the proper chevron element
        let chevron = document.querySelector('#' + chevronId)

        console.log(chevron)

        // toggle the chevron icon
        chevron?.classList.toggle('rotate-chevron');
      });
    });*/
  }

  authenticated() {
    return this.authenticationService.isAuthenticated()
  }

  getAuthenticatedUser() {
    return this.authenticationService.getAuthenticatedUser()
  }

  deauthenticate() {
    this.authenticationService.deauthenticate().subscribe()
  }
}
