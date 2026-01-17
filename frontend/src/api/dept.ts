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
  return request.post('/api/v1/depts/list', params);
};

export const getDeptDetailApi = (id: string): Promise<DeptDetailResponse> => {
  return request.get(`/api/v1/depts/detail/${id}`);
};

export const createDeptApi = (params: DeptEntity): Promise<DeptCreateResponse> => {
  return request.post('/api/v1/depts/create', params);
};

export const updateDeptApi = (params: DeptEntity): Promise<DeptCreateResponse> => {
  return request.patch('/api/v1/depts/edit', params);
};

export const deleteDeptApi = (ids: string): Promise<any> => {
  return request.delete(`/api/v1/depts/delete?ids=${ids}`);
};

export const getDeptTreeSelectApi = (): Promise<DeptTreeSelectResponse> => {
  return request.get('/common/dept/treeselect');
};

export const uploadDeptLogoApi = (file: File): Promise<any> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/api/v1/depts/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

