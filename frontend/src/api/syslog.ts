import request from './request';
import type {
  SysLogSearchForm,
  SysLogListResponse,
  SysLogDetailResponse,
  ApiResponse
} from '../types';

export const getSysLogListApi = (searchForm: SysLogSearchForm): Promise<SysLogListResponse> => {
  return request.post('/api/v1/sys-logs/list', searchForm);
};

export const getSysLogDetailApi = (id: string): Promise<SysLogDetailResponse> => {
  return request.get(`/api/v1/sys-logs/view?id=${id}`);
};

export const deleteSysLogApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/sys-logs/delete?ids=${ids}`);
};

export const deleteManySysLogApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/sys-logs/deletemany`, { params: { ids } });
};

