import axios from 'axios';
import { moduleRecord } from '@/api/module';
import { systemTypeRecord } from '@/api/system';

export interface roleRecord {
  guid: string;
  permission: [];
  organ: [];
  system: [];
  roleName: string;
  role: string;
}

export interface organRecord {
  id: string;
  cname: string;
  children: [];
}

export interface roleParams extends Partial<roleRecord> {
  current: number;
  pageSize: number;
}

export interface roleRes {
  list: roleRecord[];
  total: number;
}

export interface organRes {
  data: organRecord[];
}

export interface moduleRes {
  data: moduleRecord[];
}

export interface systemRes {
  data: systemTypeRecord[];
}

export function getRole(params: roleParams) {
  return axios.post<roleRes>('/xiyaaccounts/manage/role/getRole', params);
}

export function addRole(params: roleRecord) {
  return axios.post('/xiyaaccounts/manage/role/addRole', params);
}

export function deleteRole(params: string) {
  return axios.post('/xiyaaccounts/manage/role/deleteRole', params);
}

export function updateRole(params: roleRecord) {
  return axios.post('/xiyaaccounts/manage/role/updateRole', params);
}

export function getOrgan() {
  return axios.post<organRes>('/xiyaaccounts/manage/role/getOrgan');
}

export function getModule() {
  return axios.post<moduleRes>('/xiyaaccounts/manage/role/getModule');
}

export function getSystem() {
  return axios.post<systemRes>('/xiyaaccounts/manage/role/getSystem');
}
