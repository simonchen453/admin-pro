import request from './request';
import type {
  DomainEnvSearchForm,
  DomainEnvEntity,
  DomainEnvListResponse,
  DomainEnvDetailResponse,
  DomainEnvCreateResponse
} from '../types';

export const getDomainEnvListApi = (params: DomainEnvSearchForm): Promise<DomainEnvListResponse> => {
  return request.post('/admin/userDomainEnv/list', params);
};

export const getDomainEnvDetailApi = (id: string): Promise<DomainEnvDetailResponse> => {
  return request.get(`/admin/userDomainEnv/detail/${id}`);
};

export const createDomainEnvApi = (params: DomainEnvEntity): Promise<DomainEnvCreateResponse> => {
  return request.post('/admin/userDomainEnv/create', params);
};

export const updateDomainEnvApi = (params: DomainEnvEntity): Promise<DomainEnvCreateResponse> => {
  return request.patch('/admin/userDomainEnv/edit', params);
};

export const deleteDomainEnvApi = (ids: string): Promise<DomainEnvCreateResponse> => {
  return request.delete(`/admin/userDomainEnv/delete?ids=${ids}`);
};

