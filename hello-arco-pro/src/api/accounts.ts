import axios from 'axios';

export interface accountRecord {
  name: string;
  loginid: string;
  workid: string;
  status: string;
  organ: string;
  post: string;
  guid: string;
  enddate: string;
  authSystemList: [{ system: ''; role: ''; roleName: '' }];
}

export interface accountParams extends Partial<accountRecord> {
  current: number;
  pageSize: number;
}

export interface accountRes {
  list: accountRecord[];
  total: number;
}

export function getAccount(params: accountParams) {
  return axios.post<accountRes>(
    '/xiyaaccounts/manage/account/getAccount',
    params
  );
}

export function updateAccount(data: any) {
  return axios.post('/xiyaaccounts/manage/account/updateAccount', data);
}

export function saveAccount(data: any) {
  return axios.post('/xiyaaccounts/manage/account/saveAccount', data);
}

export function resetPassword(data: any) {
  return axios.post('/xiyaaccounts/manage/account/resetPassword', data);
}

export function deleteAccount(data: any) {
  return axios.post('/xiyaaccounts/manage/account/deleteAccount', data);
}

export function verifyWorkid(data: any) {
  return axios.post('/xiyaaccounts/manage/account/verifyWorkid', data);
}
