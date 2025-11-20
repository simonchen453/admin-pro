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
  const response = await request.post<ApiResponse<JobListResponse>>('/admin/job/list', searchForm);
  return response.data as unknown as JobListResponse;
};

// 获取定时任务详情
export const getJobDetailApi = async (id: string): Promise<JobDetailResponse> => {
  const response = await request.get<ApiResponse<JobDetailResponse>>(`/admin/job/detail/${id}`);
  return response.data as unknown as JobDetailResponse;
};

// 创建定时任务
export const createJobApi = async (jobData: JobEntity): Promise<ApiResponse> => {
  const response = await request.post<ApiResponse>('/admin/job', jobData);
  return response as unknown as ApiResponse;
};

// 更新定时任务
export const updateJobApi = async (jobData: JobEntity): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/job', jobData);
  return response as unknown as ApiResponse;
};

// 删除定时任务
export const deleteJobApi = async (ids: string): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>(`/admin/job/delete?ids=${ids}`);
  return response as unknown as ApiResponse;
};

// 暂停任务
export const pauseJobApi = async (id: string): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/job/pause', id);
  return response as unknown as ApiResponse;
};

// 立即执行任务
export const runJobApi = async (id: string): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/job/run', id);
  return response as unknown as ApiResponse;
};

// 重启任务
export const resumeJobApi = async (id: string): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/job/resume', id);
  return response as unknown as ApiResponse;
};

// 获取下次执行时间
export const getNextTimeApi = async (cronExpression: string): Promise<JobNextTimeResponse> => {
  const response = await request.get<ApiResponse<JobNextTimeResponse>>('/admin/job/nextTime', {
    params: { cronExpression }
  });
  return response.data as unknown as JobNextTimeResponse;
};

// 查询定时任务日志列表
export const getJobLogListApi = async (searchForm: JobLogSearchForm): Promise<JobLogListResponse> => {
  const response = await request.post<ApiResponse<JobLogListResponse>>('/admin/job/log/list', searchForm);
  return response.data as unknown as JobLogListResponse;
};

// 删除定时任务日志
export const deleteJobLogApi = async (ids: string): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>(`/admin/job/log/delete?ids=${ids}`);
  return response as unknown as ApiResponse;
};

// 清空所有定时任务日志
export const deleteAllJobLogApi = async (): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>('/admin/job/log/delete/all');
  return response as unknown as ApiResponse;
};

