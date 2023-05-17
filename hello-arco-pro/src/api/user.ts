import axios from 'axios';
import type { RouteRecordNormalized } from 'vue-router';
import { UserState } from '@/store/modules/user/types';
import { getToken } from '@/utils/auth';

export interface LoginData {
  loginid: string;
  password: string;
  syscode: [];
}

export interface LoginRes {
  guid: string;
  token: string;
}

export function login(data: LoginData) {
  return axios.post<LoginRes>('/xiyaaccounts/account/checkLogin', data);
}

export function logout() {
  return axios.post<LoginRes>('/xiyaaccounts/account/logout');
}

export function getAccountInfo() {
  return axios.post<UserState>('/xiyaaccounts/account/getAccountInfo');
}
