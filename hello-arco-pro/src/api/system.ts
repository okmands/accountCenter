import axios from 'axios';

export interface systemTypeRecord {
  id: string;
  syscode: string;
  sysname: string;
  loginUrl: string;
  status: string;
  roleApi: string;
  apiParameter: string;
  role: [];
}

export interface systemParams extends Partial<systemTypeRecord> {
  current: number;
  pageSize: number;
}

export interface systemTypeRes {
  list: systemTypeRecord[];
  total: number;
}

export function getSystemType(params: systemParams) {
  return axios.post<systemTypeRes>(
    '/xiyaaccounts/manage/system/getSystemType',
    params
  );
}

export function getSystemTypeList() {
  return axios.post('/xiyaaccounts/manage/system/getSystemTypeList');
}

export function addSystemType(data: any) {
  return axios.post('/xiyaaccounts/manage/system/addSystemType', data);
}

export function updateSystemType(data: any) {
  return axios.post('/xiyaaccounts/manage/system/updateSystemType', data);
}

export function deleteSystemType(data: any) {
  return axios.post('/xiyaaccounts/manage/system/deleteSystemType', data);
}
