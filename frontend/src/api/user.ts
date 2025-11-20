import request from './request';
import type { 
  UserSearchForm, 
  UserListResponse, 
  UserCreateRequest, 
  UserUpdateRequest, 
  UserDetailResponse, 
  UserResetPasswordRequest,
  UserStatusChangeResponse,
  ApiResponse,
  DeptEntity,
  RoleEntity,
  PostEntity
} from '../types';

// 用户列表查询
export const getUserListApi = async (searchForm: UserSearchForm): Promise<UserListResponse> => {
  const response = await request.post<ApiResponse<UserListResponse>>('/admin/user/list', searchForm);
  return response.data;
};

// 创建用户
export const createUserApi = async (userData: UserCreateRequest): Promise<ApiResponse> => {
  const response = await request.post<ApiResponse>('/admin/user', userData);
  return response;
};

// 更新用户
export const updateUserApi = async (userData: UserUpdateRequest): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/user', userData);
  return response;
};

// 激活用户
export const activeUserApi = async (userDomain: string, userId: string): Promise<UserStatusChangeResponse> => {
  const response = await request.patch<ApiResponse>(`/admin/user/active/${userDomain}/${userId}`);
  return {
    success: response.restCode === '200' || response.restCode === '0',
    message: response.message
  };
};

// 停用用户
export const inactiveUserApi = async (userDomain: string, userId: string): Promise<UserStatusChangeResponse> => {
  const response = await request.patch<ApiResponse>(`/admin/user/inactive/${userDomain}/${userId}`);
  return {
    success: response.restCode === '200' || response.restCode === '0',
    message: response.message
  };
};

// 重置密码
export const resetPasswordApi = async (resetData: UserResetPasswordRequest): Promise<ApiResponse> => {
  const response = await request.patch<ApiResponse>('/admin/user/resetpwd', resetData);
  return response;
};

// 获取用户详情
export const getUserDetailApi = async (userDomain: string, userId: string): Promise<UserDetailResponse> => {
  const response = await request.get<ApiResponse<UserDetailResponse>>(`/admin/user/detail/${userDomain}/${userId}`);
  return response.data;
};

// 获取部门列表（用于下拉选择）
export const getDeptListApi = async (): Promise<DeptEntity[]> => {
  const response = await request.get<ApiResponse<DeptEntity[]>>('/admin/dept/list');
  return response.data;
};

// 获取部门树结构（用于树形选择）
export const getDeptTreeSelectApi = async (): Promise<any[]> => {
  const response = await request.get<ApiResponse<any[]>>('/common/dept/treeselect');
  return response.data;
};

// 获取角色列表（用于下拉选择）
export const getRoleListApi = async (): Promise<RoleEntity[]> => {
  const response = await request.get<ApiResponse<RoleEntity[]>>('/admin/role/list');
  return response.data;
};

// 获取岗位列表（用于下拉选择）
export const getPostListApi = async (): Promise<PostEntity[]> => {
  const response = await request.get<ApiResponse<PostEntity[]>>('/admin/post/list');
  return response.data;
};

// 获取用户管理页面准备数据（部门、角色、岗位）
export const getUserPrepareDataApi = async (): Promise<{
  posts: PostEntity[];
  roles: RoleEntity[];
  depts: DeptEntity[];
}> => {
  const response = await request.get<ApiResponse<{
    posts: PostEntity[];
    roles: RoleEntity[];
    depts: DeptEntity[];
  }>>('/admin/user/prepare');
  return response.data;
};

// 删除用户（支持批量删除）
export const deleteUserApi = async (userIds: string): Promise<ApiResponse> => {
  const response = await request.delete<ApiResponse>(`/admin/user/delete?users=${userIds}`);
  return response;
};

// 获取用户域列表
export const getDomainListApi = async (): Promise<Array<{ id: string; name: string;display: string }>> => {
  const response = await request.get<ApiResponse<Array<{ id: string; name: string;display: string }>>>('/common/domains');
  return response.data;
};
