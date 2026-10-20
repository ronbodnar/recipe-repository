export interface Group {
  id: number;
  ownerId: number;
  name: string;
  description: string;
  createdAt: Date;
  lastModifiedAt: Date;
  memberCount: number;
}

export interface GroupMember {
  id: number;
  userId: number;
  username: string;
  fullName: string;
  profileImageId: string;
  role: string;
  joinedAt: Date;
}

export interface GroupInvitation {
  id: number;
  invitedUsername: string;
  invitedProfileImageId: string;
  invitedByUsername: string;
  invitedByProfileImageId: string;
  status: string;
  invitedAt: Date;
}
