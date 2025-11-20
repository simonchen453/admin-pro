import request from './request';
import type { 
  ConfigSearchForm, 
  ConfigListResponse, 
  ConfigDetailResponse,
  ConfigCreateResponse,
  ConfigEntity,
  ApiResponse
} from '../types';

export const getConfigListApi = (params: ConfigSearchForm): Promise<ConfigListResponse> => {
  return request.post('/admin/config/list', params);
};

export const getConfigDetailApi = (id: string): Promise<ConfigDetailResponse> => {
  return request.get(`/admin/config/detail/${id}`);
};

export const updateConfigApi = (params: ConfigEntity): Promise<ConfigCreateResponse> => {
  return request.patch('/admin/config/edit', params);
};

export const createConfigApi = (params: ConfigEntity): Promise<ConfigCreateResponse> => {
  return request.post('/admin/config/create', params);
};

export const deleteConfigApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/admin/config/delete?ids=${ids}`);
};

