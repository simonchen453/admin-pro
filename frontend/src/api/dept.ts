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
  return request.post('/admin/dept/list', params);
};

export const getDeptDetailApi = (id: string): Promise<DeptDetailResponse> => {
  return request.get(`/admin/dept/detail/${id}`);
};

export const createDeptApi = (params: DeptEntity): Promise<DeptCreateResponse> => {
  return request.post('/admin/dept/create', params);
};

export const updateDeptApi = (params: DeptEntity): Promise<DeptCreateResponse> => {
  return request.patch('/admin/dept/edit', params);
};

export const deleteDeptApi = (ids: string): Promise<any> => {
  return request.delete(`/admin/dept/delete?ids=${ids}`);
};

export const getDeptTreeSelectApi = (): Promise<DeptTreeSelectResponse> => {
  return request.get('/common/dept/treeselect');
};

export const uploadDeptLogoApi = (file: File): Promise<any> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/admin/dept/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

