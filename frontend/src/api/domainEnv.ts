import request from './request';
import type {
  DomainEnvSearchForm,
  DomainEnvEntity,
  DomainEnvListResponse,
  DomainEnvDetailResponse,
  DomainEnvCreateResponse
} from '../types';

export const getDomainEnvListApi = (params: DomainEnvSearchForm): Promise<DomainEnvListResponse> => {
  return request.post('/api/v1/user-domain-envs/list', params);
};

export const getDomainEnvDetailApi = (id: string): Promise<DomainEnvDetailResponse> => {
  return request.get(`/api/v1/user-domain-envs/detail/${id}`);
};

export const createDomainEnvApi = (params: DomainEnvEntity): Promise<DomainEnvCreateResponse> => {
  return request.post('/api/v1/user-domain-envs/create', params);
};

export const updateDomainEnvApi = (params: DomainEnvEntity): Promise<DomainEnvCreateResponse> => {
  return request.patch('/api/v1/user-domain-envs/edit', params);
};

export const deleteDomainEnvApi = (ids: string): Promise<DomainEnvCreateResponse> => {
  return request.delete(`/api/v1/user-domain-envs/delete?ids=${ids}`);
};

