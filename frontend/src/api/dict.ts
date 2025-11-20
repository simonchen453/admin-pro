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
  return request.post('/admin/dict/list', params);
};

export const getDictDetailApi = (id: string): Promise<DictDetailResponse> => {
  return request.get(`/admin/dict/detail/${id}`);
};

export const updateDictApi = (params: DictEntity): Promise<DictCreateResponse> => {
  return request.patch('/admin/dict/edit', params);
};

export const createDictApi = (params: DictEntity): Promise<DictCreateResponse> => {
  return request.post('/admin/dict/create', params);
};

export const deleteDictApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/admin/dict/delete?ids=${ids}`);
};

export const activeDictApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/admin/dict/active/${id}`);
};

export const inactiveDictApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/admin/dict/inactive/${id}`);
};

