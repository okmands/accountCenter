export type RoleType = '' | '*' | 'admin' | 'user';
export interface UserState {
  workid: undefined;
  name: undefined;
  organ: undefined;
  post: undefined;
  role: RoleType;
  permission: [];
}
