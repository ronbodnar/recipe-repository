export interface UserAccount {
  id: number;
  username: string;
  email: string;
  givenName: string | null;
  familyName: string | null;
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
