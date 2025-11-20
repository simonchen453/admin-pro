import request from './request';
import type { LoginRequest, LoginResponse, ApiResponse } from '../types';

// 登录接口
export const loginApi = async (data: LoginRequest): Promise<LoginResponse> => {
  const respData = await request.post<LoginResponse>('/rest/auth/login', data);
  return respData as unknown as LoginResponse;
};

// 登出接口
export const logoutApi = async (): Promise<void> => {
  await request.post('/rest/auth/logout');
};

// 用户信息类型
export interface UserInfo {
  id: number;
  name: string;
  email: string;
  avatar?: string;
  role?: string;
  status?: 'active' | 'inactive';
}

// 获取用户信息接口
export const getUserInfoApi = async (): Promise<UserInfo> => {
  const respData = await request.get<ApiResponse<UserInfo>>('/rest/auth/userinfo');
  return respData.data;
};

// 修改密码接口
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ChangePasswordResponse {
  success: boolean;
  message: string;
}

export const changePasswordApi = async (data: ChangePasswordRequest): Promise<ChangePasswordResponse> => {
  try {
    const response = await request.post<ChangePasswordResponse>('/common/changepwd', data);
    return response as unknown as ChangePasswordResponse;
  } catch (error) {
    console.error('修改密码失败:', error);
    throw error;
  }
};
