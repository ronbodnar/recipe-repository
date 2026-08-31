export interface UserAccount {
  id: string;
  keycloakSubject: string;
  username: string;
  email: string;
  givenName: string;
  familyName: string;
  name: string;
  roles: string[];
}
