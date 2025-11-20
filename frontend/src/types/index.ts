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
  confirmPassword?: string;
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
  id?: string;
  no?: string;
  name?: string;
  parentId?: string;
  orderNum?: number;
  linkman?: string;
  phone?: string;
  email?: string;
  status?: string;
  customLogin?: string;
  logoPath?: string;
  createdDate?: string;
  children?: DeptEntity[];
}

export interface DeptSearchForm {
  name?: string;
  status?: string;
}

export interface DeptListResponse {
  data: DeptEntity[];
  restCode: string;
  message: string;
  success: boolean;
}

export interface DeptDetailResponse {
  data: DeptEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface DeptCreateResponse {
  data: DeptEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface DeptTreeSelectNode {
  id: string;
  label: string;
  children?: DeptTreeSelectNode[];
}

export interface DeptTreeSelectResponse {
  data: DeptTreeSelectNode[];
  restCode: string;
  message: string;
  success: boolean;
}

// 角色类型
export interface RoleEntity {
  id?: string;
  name: string;
  code?: string;
}

// 岗位类型
export interface PostEntity {
  id?: string;
  code: string;
  name: string;
  sort?: number;
  status: string;
  remark?: string;
  createdDate?: string;
}

// 岗位搜索表单
export interface PostSearchForm {
  code?: string;
  name?: string;
  status?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

// 岗位列表响应
export interface PostListResponse {
  data: {
    records: PostEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

// 岗位详情响应
export interface PostDetailResponse {
  data: PostEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 岗位创建/更新响应
export interface PostCreateResponse {
  data: PostEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 岗位状态常量
export const PostStatus = {
  ACTIVE: 'active',
  INACTIVE: 'inactive'
} as const;

// 用户状态常量
export const UserStatus = {
  ACTIVE: '20',
  LOCK: '30'
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

// 菜单管理相关类型
export interface MenuEntity {
  id?: string;
  parentId: string | number;
  display: string;
  name: string;
  icon?: string;
  type: string;
  orderNum?: number;
  permission?: string;
  url?: string;
  status: string;
  visible?: string;
  createdDate?: string;
  children?: MenuEntity[];
}

export interface MenuSearchForm {
  name?: string;
  status?: string;
}

export interface MenuListResponse {
  data: MenuEntity[];
  restCode: string;
  message: string;
  success: boolean;
}

export interface MenuDetailResponse {
  data: MenuEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface MenuCreateResponse {
  data: MenuEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface MenuTreeSelectNode {
  id: string;
  display: string;
  children?: MenuTreeSelectNode[];
}

export interface MenuTreeSelectResponse {
  data: MenuTreeSelectNode[];
  restCode: string;
  message: string;
  success: boolean;
}

// 菜单类型常量
export const MenuType = {
  DIRECTORY: 'M',
  MENU: 'C',
  BUTTON: 'F'
} as const;

// 菜单状态常量
export const MenuStatus = {
  ACTIVE: 'active',
  INACTIVE: 'inactive'
} as const;

// 菜单显示状态常量
export const MenuVisible = {
  SHOW: 'show',
  HIDE: 'hide'
} as const;

// 部门状态常量
export const DeptStatus = {
  ACTIVE: 'active',
  INACTIVE: 'inactive'
} as const;

// 用户域管理相关类型
export interface DomainSearchForm {
  name?: string;
  display?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface DomainEntity {
  id?: string;
  name: string;
  display: string;
}

export interface DomainListResponse {
  data: {
    records: DomainEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface DomainDetailResponse {
  data: DomainEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface DomainCreateResponse {
  data: DomainEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 用户域环境配置管理相关类型
export interface DomainEnvSearchForm {
  userDomain?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface DomainEnvEntity {
  id?: string;
  userDomain: string;
  commonRole: string;
  homePageUrl: string;
  loginUrl: string;
  description?: string;
}

export interface DomainEnvListResponse {
  data: {
    records: DomainEnvEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface DomainEnvDetailResponse {
  data: DomainEnvEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface DomainEnvCreateResponse {
  data: DomainEnvEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 参数配置管理相关类型
export interface ConfigSearchForm {
  name?: string;
  key?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface ConfigEntity {
  id?: string;
  name: string;
  key: string;
  value: string;
  remark?: string;
  system?: string;
}

export interface ConfigListResponse {
  data: {
    records: ConfigEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface ConfigDetailResponse {
  data: ConfigEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface ConfigCreateResponse {
  data: ConfigEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface DictSearchForm {
  name?: string;
  key?: string;
  status?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface DictEntity {
  id?: string;
  name: string;
  key: string;
  status: string;
  remark?: string;
  data?: DictDataEntity[];
}

export interface DictDataEntity {
  key?: string;
  value: string;
  label: string;
  order: string;
  cssClass?: string;
  status: string;
  index?: number;
}

export interface DictListResponse {
  data: {
    records: DictEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface DictDetailResponse {
  data: DictEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface DictCreateResponse {
  data: DictEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 定时任务管理相关类型
export interface JobSearchForm {
  condition?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface JobEntity {
  id?: string;
  beanName: string;
  methodName: string;
  params?: string;
  cronExpression: string;
  status: string;
  remark?: string;
  nextValidTime?: string;
}

export interface JobListResponse {
  data: {
    records: JobEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface JobDetailResponse {
  data: JobEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface JobCreateResponse {
  data: JobEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

export interface JobNextTimeResponse {
  data: string;
  restCode: string;
  message: string;
  success: boolean;
}

// 定时任务状态常量
export const JobStatus = {
  NORMAL: '1',
  PAUSE: '0'
} as const;

// 定时任务日志相关类型
export interface JobLogSearchForm {
  condition?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface JobLogEntity {
  id?: string;
  jobId: string;
  beanName: string;
  methodName: string;
  params?: string;
  status: string;
  times?: number;
  createTime?: string;
  error?: string;
}

export interface JobLogListResponse {
  data: {
    records: JobLogEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

// 定时任务日志状态常量
export const JobLogStatus = {
  SUCCESS: '0',
  FAIL: '1'
} as const;

// Session管理相关类型
export interface SessionSearchForm {
  sessionId?: string;
  userDomain?: string;
  loginName?: string;
  status?: string;
  ipAddr?: string;
  deptNo?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface SessionEntity {
  id: string;
  sessionId: string;
  userDomain: string;
  loginName: string;
  deptNo?: string;
  ipAddr?: string;
  loginLocation?: string;
  browser?: string;
  os?: string;
  status: string;
  createdDate?: string;
}

export interface SessionListResponse {
  data: {
    records: SessionEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

// Session状态常量
export const SessionStatus = {
  ACTIVE: 'active',
  SUSPEND: 'suspend',
  KILLED: 'killed'
} as const;

// 服务器信息相关类型
export interface ServerCpuInfo {
  cpuNum: number;
  used: number;
  sys: number;
  free: number;
}

export interface ServerMemInfo {
  total: number;
  used: number;
  free: number;
  usage: number;
}

export interface ServerJvmInfo {
  total: number;
  used: number;
  free: number;
  usage: number;
  name: string;
  version: string;
  startTime: string;
  runTime: string;
  home: string;
}

export interface ServerSysInfo {
  computerName: string;
  computerIp: string;
  osName: string;
  osArch: string;
  userDir: string;
}

export interface ServerSysFileInfo {
  dirName: string;
  sysTypeName: string;
  typeName: string;
  total: string;
  free: string;
  used: string;
  usage: number;
}

export interface ServerInfo {
  cpu?: ServerCpuInfo;
  mem?: ServerMemInfo;
  jvm?: ServerJvmInfo;
  sys?: ServerSysInfo;
  sysFiles?: ServerSysFileInfo[];
}

export interface ServerInfoResponse {
  data: ServerInfo;
  restCode: string;
  message: string;
  success: boolean;
}

// 系统日志管理相关类型
export interface SysLogSearchForm {
  condition?: string;
  startTime?: string;
  endTime?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface SysLogEntity {
  id?: string;
  userDomain?: string;
  userId?: string;
  loginName?: string;
  ip?: string;
  browser?: string;
  method?: string;
  operation?: string;
  params?: string;
  response?: string;
  time?: number;
  createdDate?: string;
}

export interface SysLogListResponse {
  data: {
    records: SysLogEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface SysLogDetailResponse {
  data: SysLogEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 审计日志管理相关类型
export interface AuditLogSearchForm {
  category?: string;
  module?: string;
  user?: string;
  event?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface AuditLogEntity {
  id?: string;
  category?: string;
  module?: string;
  event?: string;
  ipAddress?: string;
  status?: string;
  logDate?: string;
  userName?: string;
  beforeData?: string;
  afterData?: string;
}

export interface AuditLogListResponse {
  data: {
    records: AuditLogEntity[];
    totalCount: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

export interface AuditLogDetailResponse {
  data: AuditLogEntity;
  restCode: string;
  message: string;
  success: boolean;
  errorsMap?: Record<string, string>;
}

// 代码生成器相关类型
export interface GeneratorSearchForm {
  tableName?: string;
  pageNo?: number;
  pageSize?: number;
  totalNum?: number;
}

export interface GeneratorEntity {
  tableName: string;
  tableComment?: string;
  createdDate?: string;
  updatedDate?: string;
}

export interface GeneratorListResponse {
  data: {
    records: GeneratorEntity[];
    totalCount: number;
    totalPage?: number;
  };
  restCode: string;
  message: string;
  success: boolean;
}

// 系统信息相关类型
export interface SystemInfo {
  releaseVersion?: string | null;
  buildVersion?: string | null;
  platformName: string;
  platformShortName: string;
  copyRight: string;
}