import { Component } from '@angular/core';
import { CommonModule } from "@angular/common";
import { User } from "../account";
import { UserService } from "../account.service";

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './account-list.component.html',
  styleUrl: './account-list.component.css'
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
