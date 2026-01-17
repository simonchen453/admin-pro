import request from './request';
import type {
  DictSearchForm,
  DictEntity,
  DictListResponse,
  DictDetailResponse,
  DictCreateResponse,
  ApiResponse
} from '../types';

export const getDictListApi = (params: DictSearchForm): Promise<DictListResponse> => {
  return request.post('/api/v1/dicts/list', params);
};

export const getDictDetailApi = (id: string): Promise<DictDetailResponse> => {
  return request.get(`/api/v1/dicts/detail/${id}`);
};

export const updateDictApi = (params: DictEntity): Promise<DictCreateResponse> => {
  return request.patch('/api/v1/dicts/edit', params);
};

export const createDictApi = (params: DictEntity): Promise<DictCreateResponse> => {
  return request.post('/api/v1/dicts/create', params);
};

export const deleteDictApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/dicts/delete?ids=${ids}`);
};

export const activeDictApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/dicts/active/${id}`);
};

export const inactiveDictApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/dicts/inactive/${id}`);
};

