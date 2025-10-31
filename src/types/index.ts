import type {ReactNode} from 'react';

// 用户相关类型
export interface User {
  id: number;
  name: string;
  email: string;
  avatar?: string;
  role?: string;
  status?: 'active' | 'inactive';
  createdAt?: string;
  updatedAt?: string;
}

// 登录请求类型
export interface LoginRequest {
  userId: string;
  password: string;
  platform: string;
  captcha: string;
  captchaKey: string;
}

// 登录响应类型
export interface LoginResponse {
  user: User;
  message?: string;
}

// API响应类型
export interface ApiResponse<T = unknown> {
  restCode: string;
  message: string;
  data: T;
  success: boolean;
  errors: string[];
  errorsMap: Record<string, string>;
}

// 分页类型
export interface PaginationParams {
  page: number;
  pageSize: number;
  total?: number;
}

export interface PaginatedResponse<T> {
  list: T[];
  pagination: PaginationParams;
}

// 菜单项类型
export interface MenuItem {
  key: string;
  icon?: ReactNode;
  label: string;
  path?: string;
  children?: MenuItem[];
}

// 后端菜单项类型
export interface BackendMenuItem {
  index: string;
  title: string;
  icon: string;
  url: string;
  subs: BackendMenuItem[];
}

// 菜单响应类型
export interface MenuResponse {
  restCode: string;
  message: string;
  data: BackendMenuItem[];
  success: boolean;
}

// 统计数据类型
export interface StatisticData {
  title: string;
  value: number;
  icon: ReactNode;
  color: string;
  suffix?: string;
}

// 订单类型
export interface Order {
  id: string;
  orderId: string;
  customer: string;
  amount: number;
  status: 'pending' | 'processing' | 'completed' | 'cancelled';
  createdAt: string;
  updatedAt: string;
}

// 错误类型
export interface AppError {
  code: string;
  message: string;
  details?: Record<string, unknown>;
}

// API 错误响应类型
export interface ApiErrorResponse {
  restCode: string;
  message: string;
  success: boolean;
  errors: string[];
  errorsMap: Record<string, string>;
}

// 用户管理相关类型
export interface UserSearchForm {
  userDomain?: string;
  userId?: string;
  display?: string;
  status?: string;
  loginName?: string;
  realName?: string;
  deptId?: string;
  page?: number;
  pageSize?: number;
}

export interface UserEntity {
  userIden: {
    userDomain: string;
    userId: string;
  };
  loginName: string;
  realName: string;
  mobileNo?: string;
  email?: string;
  avatarUrl?: string;
  status: string;
  sex?: string;
  description?: string;
  deptNo?: string;
  latestLoginTime?: string;
  password?: string;
}

export interface UserListResponse {
  list: UserEntity[];
  pagination: {
    page: number;
    pageSize: number;
    total: number;
  };
}

export interface UserCreateRequest {
  userDomain: string;
  loginName: string;
  realName: string;
  mobileNo?: string;
  email?: string;
  avatarUrl?: string;
  status: string;
  sex?: string;
  description?: string;
  deptId?: string;
  password: string;
  roleIds: string[];
  postIds: string[];
}

export interface UserUpdateRequest extends UserCreateRequest {
  userId: string;
}

export interface UserDetailResponse {
  userDomain: string;
  userId: string;
  loginName: string;
  realName: string;
  mobileNo?: string;
  email?: string;
  avatarUrl?: string;
  status: string;
  sex?: string;
  description?: string;
  deptNo?: string;
  deptId?: string;
  latestLoginTime?: string;
  roleIds: string[];
  postIds: string[];
}

export interface UserResetPasswordRequest {
  userDomain: string;
  userId: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UserStatusChangeResponse {
  success: boolean;
  message?: string;
}

// 部门类型
export interface DeptEntity {
  id: string;
  no: string;
  name: string;
  parentId: string;
}

// 角色类型
export interface RoleEntity {
  id?: string;
  name: string;
  code?: string;
}

// 岗位类型
export interface PostEntity {
  id: string;
  code: string;
  name: string;
}

// 用户状态常量
export const UserStatus = {
  ACTIVE: 'ACTIVE',
  LOCK: 'LOCK',
  INACTIVE: 'INACTIVE'
} as const;

// 角色状态常量
export const RoleStatus = {
  ACTIVE: 'active',
  INACTIVE: 'inactive'
} as const;

// 系统配置常量
export const SystemConfig = {
  YES: 'true',
  NO: 'false'
} as const;

// 角色搜索表单
export interface RoleSearchForm {
  name?: string;
  display?: string;
  status?: string;
  system?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

// 角色实体
export interface RoleEntity {
  id?: string;
  name: string;
  display: string;
  status: string;
  system: string;
  menuNames?: string[];
  code?: string;
}

// 角色列表响应
export interface RoleListResponse {
  data: {
    records: RoleEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

// 角色详情响应
export interface RoleDetailResponse {
  data: RoleEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 角色创建/更新响应
export interface RoleCreateResponse {
  data: RoleEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 菜单树节点
export interface MenuTreeNode {
  id: string;
  label: string;
  children?: MenuTreeNode[];
}

// 菜单树响应
export interface MenuTreeResponse {
  data: MenuTreeNode[];
  restCode: string;
  message: string;
  success: boolean;
}

// 角色菜单树响应
export interface RoleMenuTreeResponse {
  data: {
    menus: MenuTreeNode[];
    checkedKeys: string[];
  };
  restCode: string;
  message: string;
  success: boolean;
}
