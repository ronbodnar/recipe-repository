export interface UserRegistrationRequest {
  username: string;
  email: string;
  givenName: string;
  familyName: string;
  password: string;
  confirmPassword: string;
}
