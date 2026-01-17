import request from './request';
import type {
  DomainSearchForm,
  DomainEntity,
  DomainListResponse,
  DomainDetailResponse,
  DomainCreateResponse
} from '../types';

export const getDomainListApi = (params: DomainSearchForm): Promise<DomainListResponse> => {
  return request.post('/api/v1/domains', params);
};

export const getDomainDetailApi = (id: string): Promise<DomainDetailResponse> => {
  return request.get(`/api/v1/domains/${id}`);
};

export const createDomainApi = (params: DomainEntity): Promise<DomainCreateResponse> => {
  return request.post('/api/v1/domains/create', params);
};

export const updateDomainApi = (params: DomainEntity): Promise<DomainCreateResponse> => {
  return request.put(`/api/v1/domains/${params.id}`, params);
};

export const deleteDomainApi = (id: string): Promise<DomainCreateResponse> => {
  return request.delete(`/api/v1/domains/${id}`);
};

