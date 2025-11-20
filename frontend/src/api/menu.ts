import request from './request';
import type { 
  BackendMenuItem,
  MenuEntity,
  MenuSearchForm,
  MenuListResponse,
  MenuDetailResponse,
  MenuCreateResponse,
  ApiResponse
} from '../types';

// 获取菜单列表（用于导航）
export const getMenuList = async (): Promise<BackendMenuItem[]> => {
  try {
    const response = await request.get<BackendMenuItem[]>('/common/menus');
    return response.data || [];
  } catch (error) {
    console.error('获取菜单列表失败:', error);
    return [];
  }
};

// 获取菜单管理列表（树形结构）
export const getMenuTreeListApi = (params?: MenuSearchForm): Promise<MenuListResponse> => {
  return request.post('/admin/menu/list', params || {});
};

// 获取菜单详情
export const getMenuDetailApi = (id: string): Promise<MenuDetailResponse> => {
  return request.get(`/admin/menu/detail/${id}`);
};

// 创建菜单
export const createMenuApi = (params: MenuEntity): Promise<MenuCreateResponse> => {
  return request.post('/admin/menu/create', params);
};

// 更新菜单
export const updateMenuApi = (params: MenuEntity): Promise<MenuCreateResponse> => {
  return request.patch('/admin/menu/edit', params);
};

// 删除菜单
export const deleteMenuApi = (id: string): Promise<ApiResponse<{ success: boolean }>> => {
  return request.delete(`/admin/menu/delete/${id}`);
};

// 获取菜单树选择器数据
export const getMenuTreeSelectApi = (): Promise<MenuListResponse> => {
  return request.post('/admin/menu/list', {});
};
