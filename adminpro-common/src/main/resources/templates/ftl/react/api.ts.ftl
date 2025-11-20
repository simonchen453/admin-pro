import request from './request';
import type { 
  ${className}SearchForm, 
  ${className}ListResponse, 
  ${className}CreateRequest, 
  ${className}UpdateRequest, 
  ${className}DetailResponse,
  ApiResponse
} from '../types';

export const get${className}ListApi = async (searchForm: ${className}SearchForm): Promise<${className}ListResponse> => {
  const response = await request.post<ApiResponse<${className}ListResponse>>('/admin/${classname}/list', searchForm);
  return response.data;
};

export const get${className}DetailApi = async (id: string | number): Promise<${className}DetailResponse> => {
  const response = await request.get<ApiResponse<${className}DetailResponse>>(`/admin/${classname}/detail/<#noparse>${id}</#noparse>`);
  return response.data;
};

export const create${className}Api = async (data: ${className}CreateRequest): Promise<ApiResponse> => {
  const response = await request.post<ApiResponse>('/admin/${classname}', data);
  return response;
};

export const update${className}Api = async (data: ${className}UpdateRequest): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/${classname}', data);
  return response;
};

export const delete${className}Api = async (ids: string | number): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>(`/admin/${classname}/delete?ids=<#noparse>${ids}</#noparse>`);
  return response;
};

