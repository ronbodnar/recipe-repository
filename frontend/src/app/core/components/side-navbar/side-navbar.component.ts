import { Component } from '@angular/core';
import { fromEvent, throttleTime } from 'rxjs';

@Component({
  selector: 'app-side-navbar',
  standalone: true,
  imports: [],
  templateUrl: './side-navbar.component.html',
  styleUrl: './side-navbar.component.css',
})
export class SideNavbarComponent { 

  private isAnimating: boolean = false;

  ngOnInit(): void {
    let collapseChevrons = document.querySelectorAll('.collapse');
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
  }
}
