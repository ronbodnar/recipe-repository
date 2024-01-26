import { Component } from '@angular/core';
import { CommonModule } from "@angular/common";
import { User } from "../user";
import { UserService } from "../user.service";

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent {
  users!: User[];

  constructor(private userService: UserService) {
  }

  ngOnInit() {
    this.userService.findAll().subscribe(data => {
      this.users = data
    });
  }

  removeUser(user: User): void {
    this.userService.remove(user).subscribe()
  }
}
