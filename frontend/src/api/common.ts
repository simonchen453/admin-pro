import request from './request';
import type { ApiResponse, SystemInfo } from '../types';

export const getSystemInfoApi = (): Promise<ApiResponse<SystemInfo>> => {
  return request.get('/common/info');
};

export interface StatisticsData {
  userCount: number;
  roleCount: number;
  deptCount: number;
  sessionCount: number;
}

export const getStatisticsApi = (): Promise<ApiResponse<StatisticsData>> => {
  return request.get('/common/statistics');
};

