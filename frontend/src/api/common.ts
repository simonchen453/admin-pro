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

export interface RecentActivity {
  id: string;
  type: 'login' | 'operation' | 'system';
  title: string;
  description: string;
  time: string;
  user?: string;
}

export const getRecentActivitiesApi = (limit: number = 10): Promise<ApiResponse<RecentActivity[]>> => {
  return request.get(`/common/recent-activities?limit=${limit}`);
};

