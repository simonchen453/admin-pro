import request from './request';
import type {
  SessionSearchForm,
  SessionListResponse,
  ApiResponse
} from '../types';

export const getSessionListApi = (searchForm: SessionSearchForm): Promise<SessionListResponse> => {
  return request.post('/api/v1/tools/sessions', searchForm);
};

export const suspendSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/tools/sessions/${id}/suspend`);
};

export const unsuspendSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/tools/sessions/${id}/unsuspend`);
};

export const killSessionApi = (id: string): Promise<ApiResponse> => {
  return request.patch(`/api/v1/tools/sessions/${id}/kill`);
};

