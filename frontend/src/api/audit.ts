import request from './request';
import type {
  AuditLogSearchForm,
  AuditLogListResponse,
} from '../types';

export const getAuditLogListApi = (searchForm: AuditLogSearchForm): Promise<AuditLogListResponse> => {
  return request.post('/api/v1/tools/audit-logs', searchForm);
};

