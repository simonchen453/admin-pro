// 环境变量配置
export const config = {
  // API配置
  API_BASE_URL: import.meta.env.VITE_API_BASE || '/api',
  
  // 应用配置
  APP_TITLE: import.meta.env.VITE_APP_TITLE || 'JEE React 管理系统',
  
  // 开发环境
  IS_DEV: import.meta.env.DEV,
  
  // 生产环境
  IS_PROD: import.meta.env.PROD,
  
  // 构建时间
  BUILD_TIME: import.meta.env.VITE_BUILD_TIME || new Date().toISOString(),
} as const;

export default config;
