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

// 导入用户
export const importUserApi = async (file: File): Promise<ApiResponse> => {
  const formData = new FormData();
  formData.append('file', file);
  const response = await request.post<ApiResponse>('/admin/user/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response;
};

// 导出用户（根据选中的用户ID）
export const exportUserApi = async (userIds: string): Promise<void> => {
  const axios = (await import('axios')).default;
  try {
    const response = await axios.get(`${import.meta.env.VITE_API_BASE || '/api'}/admin/user/export?ids=${userIds}`, {
      responseType: 'blob',
      withCredentials: true,
    });
    
    if (response.data.type === 'application/json') {
      const text = await response.data.text();
      const errorData = JSON.parse(text);
      throw new Error(errorData.message || '导出失败');
    }
    
    const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `用户数据_${new Date().getTime()}.xls`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error: any) {
    if (error.response?.data?.type === 'application/json') {
      const text = await error.response.data.text();
      const errorData = JSON.parse(text);
      throw new Error(errorData.message || '导出失败');
    }
    throw error;
  }
};

// 导出所有用户（根据当前搜索条件）
export const exportAllUserApi = async (searchForm: UserSearchForm): Promise<void> => {
  const axios = (await import('axios')).default;
  const params = new URLSearchParams();
  if (searchForm.userDomain) params.append('userDomain', searchForm.userDomain);
  if (searchForm.loginName) params.append('loginName', searchForm.loginName);
  if (searchForm.realName) params.append('realName', searchForm.realName);
  if (searchForm.status) params.append('status', searchForm.status);
  if (searchForm.deptId) params.append('deptId', searchForm.deptId);
  if (searchForm.page) params.append('page', searchForm.page.toString());
  if (searchForm.pageSize) params.append('pageSize', searchForm.pageSize.toString());
  
  try {
    const response = await axios.get(`${import.meta.env.VITE_API_BASE || '/api'}/admin/user/excelAll?${params.toString()}`, {
      responseType: 'blob',
      withCredentials: true,
    });
    
    if (response.data.type === 'application/json') {
      const text = await response.data.text();
      const errorData = JSON.parse(text);
      throw new Error(errorData.message || '导出失败');
    }
    
    const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `用户数据_${new Date().getTime()}.xls`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error: any) {
    if (error.response?.data?.type === 'application/json') {
      const text = await error.response.data.text();
      const errorData = JSON.parse(text);
      throw new Error(errorData.message || '导出失败');
    }
    throw error;
  }
};