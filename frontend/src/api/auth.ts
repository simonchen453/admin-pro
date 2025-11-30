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

// 获取当前登录用户详细信息
export const getCurrentUserInfoApi = async (): Promise<any> => {
  const respData = await request.get<ApiResponse<any>>('/rest/auth/userinfo');
  return respData.data;
};

// 修改密码接口
export interface ChangePasswordRequest {
  oldPwd: string;
  newPwd: string;
  confirmNewPwd: string;
}

export const changePasswordApi = async (data: ChangePasswordRequest): Promise<ApiResponse<any>> => {
  try {
    const response = await request.patch<ApiResponse<any>>('/common/changepwd', data);
    return response;
  } catch (error) {
    console.error('修改密码失败:', error);
    throw error;
  }
};

export interface UpdateProfileRequest {
  realName?: string;
  mobileNo?: string;
  email?: string;
  avatarUrl?: string;
  sex?: string;
  description?: string;
}

export const updateProfileApi = async (data: UpdateProfileRequest): Promise<ApiResponse<any>> => {
  const response = await request.patch<ApiResponse<any>>('/rest/auth/profile', data);
  return response;
};

// 密码规则类型
export interface PasswordRule {
  minLength: number;
  maxLength?: number;
  requireLowerCase: boolean;
  requireUpperCase: boolean;
  requireDigit: boolean;
  requireSpecialChar: boolean;
  specialChars: string;
  description?: string;
}

// 获取密码规则接口
export const getPasswordRuleApi = async (): Promise<PasswordRule> => {
  const respData = await request.get<ApiResponse<PasswordRule>>('/rest/auth/password-rule');
  return respData.data;
};
