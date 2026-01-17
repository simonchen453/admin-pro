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
  return request.post('/api/v1/configs/list', params);
};

export const getConfigDetailApi = (id: string): Promise<ConfigDetailResponse> => {
  return request.get(`/api/v1/configs/detail/${id}`);
};

export const updateConfigApi = (params: ConfigEntity): Promise<ConfigCreateResponse> => {
  return request.patch('/api/v1/configs/edit', params);
};

export const createConfigApi = (params: ConfigEntity): Promise<ConfigCreateResponse> => {
  return request.post('/api/v1/configs/create', params);
};

export const deleteConfigApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/configs/delete?ids=${ids}`);
};

