import request from './request';
import type {
  DomainSearchForm,
  DomainEntity,
  DomainListResponse,
  DomainDetailResponse,
  DomainCreateResponse
} from '../types';

export const getDomainListApi = (params: DomainSearchForm): Promise<DomainListResponse> => {
  return request.post('/admin/domain/list', params);
};

export const getDomainDetailApi = (id: string): Promise<DomainDetailResponse> => {
  return request.get(`/admin/domain/detail/${id}`);
};

export const createDomainApi = (params: DomainEntity): Promise<DomainCreateResponse> => {
  return request.post('/admin/domain/create', params);
};

export const updateDomainApi = (params: DomainEntity): Promise<DomainCreateResponse> => {
  return request.patch('/admin/domain/edit', params);
};

