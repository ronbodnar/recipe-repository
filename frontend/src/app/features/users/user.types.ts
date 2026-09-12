export interface UserAccount {
  id: number;
  username: string;
  email: string;
  givenName: string;
  familyName: string;
  profileImageId: string | null;
  roles: string[];
  createdAt: Date;
  lastModifiedAt: Date;
}

export interface UserAccountSummary {
  id: number;
  username: string;
  profileImageId: string | null;
}
