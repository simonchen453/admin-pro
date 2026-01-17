import request from './request';
import type {
  ApiResponse,
  PostSearchForm,
  PostListResponse,
  PostDetailResponse,
  PostCreateResponse,
  PostEntity
} from '../types';

// 查询岗位列表
export const getPostListApi = (params: PostSearchForm): Promise<PostListResponse> => {
  return request.post('/api/v1/posts/search', params);
};

// 获取岗位详情
export const getPostDetailApi = (id: string): Promise<PostDetailResponse> => {
  return request.get(`/api/v1/posts/${id}`);
};

// 创建岗位
export const createPostApi = (params: PostEntity): Promise<PostCreateResponse> => {
  return request.post('/api/v1/posts', params);
};

// 更新岗位
export const updatePostApi = (params: PostEntity): Promise<PostCreateResponse> => {
  return request.put(`/api/v1/posts/${params.id}`, params);
};

// 删除岗位
export const deletePostApi = (id: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/posts/${id}`);
};

// 批量删除岗位
export const batchDeletePostApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/api/v1/posts?ids=${ids}`);
};

