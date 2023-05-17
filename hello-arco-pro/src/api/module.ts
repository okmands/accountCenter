import axios from 'axios';

export interface moduleRecord {
  guid: string;
  title: string;
  name: string;
  link: string;
  order: string;
  children: [];
}

export interface moduleRes {
  list: moduleRecord[];
  total: number;
}

export interface moduleParams {
  current: number;
  pageSize: number;
}

export function getModule(params: moduleParams) {
  return axios.post('/xiyaaccounts/manage/module/getModule', params);
}

export function addModule(data: any) {
  return axios.post('/xiyaaccounts/manage/module/addModule', data);
}

export function updateModule(data: any) {
  return axios.post('/xiyaaccounts/manage/module/updateModule', data);
}

export function deleteModule(data: any) {
  return axios.post('/xiyaaccounts/manage/module/deleteModule', data);
}
