// 环境变量配置
export const config = {
  // API配置 (优先使用 window._env_ 中的运行时配置，其次使用构建时注入的环境变量)
  API_BASE_URL: window._env_?.API_BASE_URL || import.meta.env.VITE_API_BASE || '',

  // 应用配置
  APP_TITLE: window._env_?.APP_TITLE || import.meta.env.VITE_APP_TITLE || 'Admin Pro 管理系统',

  // 路由基础路径 (用于 BrowserRouter 的 basename)
  BASE_PATH: window._env_?.BASE_PATH || import.meta.env.VITE_BASE_PATH || '/adminpro',

  // 开发环境
  IS_DEV: import.meta.env.DEV,

  // 生产环境
  IS_PROD: import.meta.env.PROD,

  // 构建时间
  BUILD_TIME: import.meta.env.VITE_BUILD_TIME || new Date().toISOString(),
} as const;


export default config;
