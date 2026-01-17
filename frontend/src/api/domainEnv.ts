import request from './request';
import type {
  DomainEnvSearchForm,
  DomainEnvEntity,
  DomainEnvListResponse,
  DomainEnvDetailResponse,
  DomainEnvCreateResponse
} from '../types';

export const getDomainEnvListApi = (params: DomainEnvSearchForm): Promise<DomainEnvListResponse> => {
  return request.post('/api/v1/domain-envs/list', params);
};

export const getDomainEnvDetailApi = (id: string): Promise<DomainEnvDetailResponse> => {
  return request.get(`/api/v1/domain-envs/${id}`);
};

export const createDomainEnvApi = (params: DomainEnvEntity): Promise<DomainEnvCreateResponse> => {
  return request.post('/api/v1/domain-envs/create', params);
};

export const updateDomainEnvApi = (params: DomainEnvEntity): Promise<DomainEnvCreateResponse> => {
  return request.put(`/api/v1/domain-envs/${params.id}`, params);
};

export const deleteDomainEnvApi = (ids: string): Promise<DomainEnvCreateResponse> => {
  return request.delete(`/api/v1/domain-envs?ids=${ids}`);
};

