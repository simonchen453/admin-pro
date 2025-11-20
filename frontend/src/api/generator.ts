import request from './request';
import type { 
  GeneratorSearchForm, 
  GeneratorListResponse
} from '../types';

const baseURL = import.meta.env.VITE_API_BASE || '/api';

export const getGeneratorListApi = async (searchForm: GeneratorSearchForm): Promise<GeneratorListResponse> => {
  const response = await request.get<GeneratorListResponse>('/admin/generator/list', {
    params: {
      tableName: searchForm.tableName,
      pageNo: searchForm.pageNo,
      pageSize: searchForm.pageSize
    }
  });
  return response as unknown as GeneratorListResponse;
};

export const batchGenCodeApi = async (tables: string): Promise<void> => {
  const url = `${baseURL}/admin/generator/batchGenCode?tables=${encodeURIComponent(tables)}`;
  window.open(url, '_blank');
};

export const genAllCodeApi = async (): Promise<void> => {
  const url = `${baseURL}/admin/generator/genAll`;
  window.open(url, '_blank');
};

