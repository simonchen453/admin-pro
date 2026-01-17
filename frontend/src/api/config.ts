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
  return request.post('/api/v1/tools/config', params);
};

export const getConfigDetailApi = (id: string): Promise<ConfigDetailResponse> => {
  return request.get(`/api/v1/tools/config/${id}`);
};

export const updateConfigApi = (params: ConfigEntity): Promise<ConfigCreateResponse> => {
  return request.put(`/api/v1/tools/config/${params.id}`, params);
};

export const createConfigApi = (params: ConfigEntity): Promise<ConfigCreateResponse> => {
  return request.post('/api/v1/tools/config/create', params);
};

export const deleteConfigApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/tools/config?ids=${ids}`);
};

