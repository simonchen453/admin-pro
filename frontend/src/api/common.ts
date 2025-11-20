import request from './request';
import type { ApiResponse, SystemInfo } from '../types';

export const getSystemInfoApi = (): Promise<ApiResponse<SystemInfo>> => {
  return request.get('/common/info');
};

