import request from './request';
import type {
  SessionSearchForm,
  SessionListResponse,
  ApiResponse
} from '../types';

export const getSessionListApi = (searchForm: SessionSearchForm): Promise<SessionListResponse> => {
  return request.post('/api/v1/sessions/list', searchForm);
};

export const suspendSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/sessions/suspend/${id}`);
};

export const unsuspendSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/sessions/unsuspend/${id}`);
};

export const killSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/sessions/kill/${id}`);
};

