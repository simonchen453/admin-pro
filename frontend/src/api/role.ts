import request from './request';
import type { ApiResponse } from '../types';

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

// 查询角色列表
export const getRoleListApi = (params: RoleSearchForm): Promise<RoleListResponse> => {
  return request.post('/admin/role/list', params);
};

// 获取角色详情
export const getRoleDetailApi = (id: string): Promise<RoleDetailResponse> => {
  return request.get(`/admin/role/detail/${id}`);
};

// 创建角色
export const createRoleApi = (params: RoleEntity): Promise<RoleCreateResponse> => {
  return request.post('/admin/role/create', params);
};

// 更新角色
export const updateRoleApi = (params: RoleEntity): Promise<RoleCreateResponse> => {
  return request.patch('/admin/role/edit', params);
};

// 删除角色
export const deleteRoleApi = (ids: string): Promise<ApiResponse<{ success: boolean }>> => {
  return request.delete(`/admin/role/delete?ids=${ids}`);
};

// 获取菜单树结构
export const getMenuTreeApi = (): Promise<MenuTreeResponse> => {
  return request.get('/common/menu/treeselect');
};

// 根据角色获取菜单树结构
export const getRoleMenuTreeApi = (roleName: string): Promise<RoleMenuTreeResponse> => {
  return request.get(`/common/menu/roleMenuTreeSelect/${roleName}`);
};
