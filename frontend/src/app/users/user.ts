export class User {
  id!: string;

  username!: string;

  email!: string;

  firstName!: string;
  lastName!: string;

  roles!: string[];

  enabled!: boolean;

  setFromJson(response: any): void {
    this.id = response.id;
    this.username = response.username;
    this.email = response.email;
    this.firstName = response.firstName;
    this.lastName = response.lastName;
    this.roles = response.authorities;
    this.enabled = response.enabled;
  }
}
