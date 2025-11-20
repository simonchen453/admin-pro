import request from './request';
import type {
  AuditLogSearchForm,
  AuditLogListResponse,
} from '../types';

export const getAuditLogListApi = (searchForm: AuditLogSearchForm): Promise<AuditLogListResponse> => {
  return request.post('/admin/audit/list', searchForm);
};

