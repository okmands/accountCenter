import axios from 'axios';

export interface userPropertyRecord {
  workId: string;
  name: string;
  organ: string;
  post: string;
  authSystemList: [];
}

export function getUserProperty() {
  return axios.post('/xiyaaccounts/manage/user/getUserProperty');
}
