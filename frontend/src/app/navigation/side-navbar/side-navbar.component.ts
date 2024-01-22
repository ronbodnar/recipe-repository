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
  rotateChevron(event: any): void {
    console.log(event);

    if (event.children.length > 0)
      event.children[0].childNodes[1].classList.toggle('rotate-chevron');
    fromEvent(event, 'click')
      .pipe(throttleTime(2000))
      .subscribe((red) => {
        console.log(red);
      });
  }
}
