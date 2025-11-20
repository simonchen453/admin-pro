import request from './request';
import type { ServerInfo, ApiResponse } from '../types';

export const getServerInfoApi = async (): Promise<ServerInfo> => {
  const response = await request.get<ApiResponse<ServerInfo>>('/admin/server/detail');
  return response.data as unknown as ServerInfo;
};

