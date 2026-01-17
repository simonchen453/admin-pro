import request from './request';
import type {
  DeptSearchForm,
  DeptEntity,
  DeptListResponse,
  DeptDetailResponse,
  DeptCreateResponse,
  DeptTreeSelectResponse
} from '../types';

export const getDeptListApi = (params: DeptSearchForm): Promise<DeptListResponse> => {
  return request.post('/api/v1/departments/search', params);
};

export const getDeptDetailApi = (id: string): Promise<DeptDetailResponse> => {
  return request.get(`/api/v1/departments/${id}`);
};

export const createDeptApi = (params: DeptEntity): Promise<DeptCreateResponse> => {
  return request.post('/api/v1/departments', params);
};

export const updateDeptApi = (params: DeptEntity): Promise<DeptCreateResponse> => {
  return request.put(`/api/v1/departments/${params.id}`, params);
};

export const deleteDeptApi = (ids: string): Promise<any> => {
  return request.delete(`/api/v1/departments?ids=${ids}`);
};

export const getDeptTreeSelectApi = (): Promise<DeptTreeSelectResponse> => {
  return request.get('/api/v1/departments/tree');
};

export const uploadDeptLogoApi = (file: File): Promise<any> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/api/v1/departments/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

