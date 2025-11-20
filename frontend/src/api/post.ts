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
  return request.post('/admin/post/list', params);
};

// 获取岗位详情
export const getPostDetailApi = (id: string): Promise<PostDetailResponse> => {
  return request.get(`/admin/post/detail/${id}`);
};

// 创建岗位
export const createPostApi = (params: PostEntity): Promise<PostCreateResponse> => {
  return request.post('/admin/post/create', params);
};

// 更新岗位
export const updatePostApi = (params: PostEntity): Promise<PostCreateResponse> => {
  return request.patch('/admin/post/edit', params);
};

// 删除岗位
export const deletePostApi = (ids: string): Promise<ApiResponse> => {
  return request.delete(`/admin/post/delete?ids=${ids}`);
};

