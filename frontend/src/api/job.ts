import request from './request';
import type {
  JobSearchForm,
  JobListResponse,
  JobEntity,
  JobDetailResponse,
  JobNextTimeResponse,
  JobLogSearchForm,
  JobLogListResponse,
  ApiResponse
} from '../types';

// 查询定时任务列表
export const getJobListApi = async (searchForm: JobSearchForm): Promise<JobListResponse> => {
  const response = await request.post<ApiResponse<JobListResponse>>('/api/v1/jobs', searchForm);
  return response.data as unknown as JobListResponse;
};

// 获取定时任务详情
export const getJobDetailApi = async (id: string): Promise<JobDetailResponse> => {
  const response = await request.get<ApiResponse<JobDetailResponse>>(`/api/v1/jobs/${id}`);
  return response.data as unknown as JobDetailResponse;
};

// 创建定时任务
export const createJobApi = async (jobData: JobEntity): Promise<ApiResponse> => {
  const response = await request.post<ApiResponse>('/api/v1/jobs', jobData);
  return response as unknown as ApiResponse;
};

// 更新定时任务
export const updateJobApi = async (jobData: JobEntity): Promise<ApiResponse> => {
  const response = await request.put<ApiResponse>(`/api/v1/jobs/${jobData.id}`, jobData);
  return response as unknown as ApiResponse;
};

// 删除定时任务
export const deleteJobApi = async (ids: string): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>(`/api/v1/jobs?ids=${ids}`);
  return response as unknown as ApiResponse;
};

// 暂停任务
export const pauseJobApi = async (id: string): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/api/v1/jobs/pause', id);
  return response as unknown as ApiResponse;
};

// 立即执行任务
export const runJobApi = async (id: string): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/api/v1/jobs/run', id);
  return response as unknown as ApiResponse;
};

// 重启任务
export const resumeJobApi = async (id: string): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/api/v1/jobs/resume', id);
  return response as unknown as ApiResponse;
};

// 获取下次执行时间
export const getNextTimeApi = async (cronExpression: string): Promise<JobNextTimeResponse> => {
  const response = await request.get<ApiResponse<JobNextTimeResponse>>('/api/v1/jobs/nextTime', {
    params: { cronExpression }
  });
  return response.data as unknown as JobNextTimeResponse;
};

// 查询定时任务日志列表
export const getJobLogListApi = async (searchForm: JobLogSearchForm): Promise<JobLogListResponse> => {
  const response = await request.post<ApiResponse<JobLogListResponse>>('/api/v1/jobs/logs', searchForm);
  return response.data as unknown as JobLogListResponse;
};

// 删除定时任务日志
export const deleteJobLogApi = async (ids: string): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>(`/api/v1/jobs/logs?ids=${ids}`);
  return response as unknown as ApiResponse;
};

// 清空所有定时任务日志
export const deleteAllJobLogApi = async (): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>('/api/v1/jobs/logs/all');
  return response as unknown as ApiResponse;
};

