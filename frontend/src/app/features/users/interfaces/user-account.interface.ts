export interface UserAccount {
  id: string;
  displayName: string;
  profileImageId: string;
  createdAt: Date;
  lastModifiedAt: Date;

  // These fields are brought in by Keycloak (or other identity provider) and are not stored in the database
  identityProviderSubject: string;
  username: string;
  email: string;
  givenName: string;
  familyName: string;
  name: string;
  roles: string[];
}
