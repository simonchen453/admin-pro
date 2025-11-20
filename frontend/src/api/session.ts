import request from './request';
import type {
  SessionSearchForm,
  SessionListResponse,
  ApiResponse
} from '../types';

export const getSessionListApi = (searchForm: SessionSearchForm): Promise<SessionListResponse> => {
  return request.post('/admin/session/list', searchForm);
};

export const suspendSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/admin/session/suspend/${id}`);
};

export const unsuspendSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/admin/session/unsuspend/${id}`);
};

export const killSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/admin/session/kill/${id}`);
};

