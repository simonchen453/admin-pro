import request from './request';
import type {
  SysLogSearchForm,
  SysLogListResponse,
  SysLogDetailResponse,
  ApiResponse
} from '../types';

export const getSysLogListApi = (searchForm: SysLogSearchForm): Promise<SysLogListResponse> => {
  return request.post('/admin/syslog/list', searchForm);
};

export const getSysLogDetailApi = (id: string): Promise<SysLogDetailResponse> => {
  return request.get(`/admin/syslog/view?id=${id}`);
};

export const deleteSysLogApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/admin/syslog/delete?ids=${ids}`);
};

export const deleteManySysLogApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/admin/syslog/deletemany`, { params: { ids } });
};

