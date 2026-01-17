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
  return request.post('/api/v1/tools/dict', params);
};

export const getDictDetailApi = (id: string): Promise<DictDetailResponse> => {
  return request.get(`/api/v1/tools/dict/${id}`);
};

export const updateDictApi = (params: DictEntity): Promise<DictCreateResponse> => {
  return request.put(`/api/v1/tools/dict/${params.id}`, params);
};

export const createDictApi = (params: DictEntity): Promise<DictCreateResponse> => {
  return request.post('/api/v1/tools/dict/create', params);
};

export const deleteDictApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/tools/dict?ids=${ids}`);
};

export const activeDictApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/tools/dict/active/${id}`);
};

export const inactiveDictApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/tools/dict/inactive/${id}`);
};

