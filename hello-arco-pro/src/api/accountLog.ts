import axios from 'axios';

export interface accountLogRecord {
  id: string;
  loginid: string;
  syscode: string;
  action: string;
  actionTime: [];
  actionContent: string;
}

export interface accountLogParams extends Partial<accountLogRecord> {
  current: number;
  pageSize: number;
}

export interface accountLogRes {
  list: accountLogRecord[];
  total: number;
}

export function getAccountLog(params: accountLogParams) {
  return axios.post<accountLogRes>(
    '/xiyaaccounts/manage/accountLog/getLog',
    params
  );
}
