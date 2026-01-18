import axios, { AxiosError, type AxiosResponse } from 'axios';
import { message } from '../components/StaticAntd';
import { useAuthStore } from '../stores/useUserStore';
import { config as appConfig } from '../config/env';

const request = axios.create({
    baseURL: appConfig.API_BASE_URL,
    timeout: 10000,
    withCredentials: true,
    headers: {
        'X-Requested-With': 'XMLHttpRequest',
    },
});

// 获取前端 base path（从 vite.config.ts 的 base 配置）
const BASE_PATH = import.meta.env.BASE_URL || '/adminpro/';

// 防止重复跳转的标志
let isRelogging = false;

// 静默请求的 URL 列表（这些请求失败时不显示错误消息，仅静默跳转）
const SILENT_AUTH_URLS = [
    '/api/v1/auth/userinfo',
    '/api/v1/menus/current-user'
];

// 检查是否是静默请求
const isSilentRequest = (url: string | undefined): boolean => {
    if (!url) return false;
    return SILENT_AUTH_URLS.some(silentUrl => url.includes(silentUrl));
};

// 统一处理认证失败
const handleAuthFailure = (msg: string = '会话已过期，请重新登录', showMessage: boolean = true) => {
    if (isRelogging) return;
    isRelogging = true;

    // 清除本地认证状态
    try {
        const { clearAuth } = useAuthStore.getState();
        clearAuth();
    } catch {
        // 忽略错误
    }

    if (typeof window !== 'undefined') {
        // 只有非静默模式才显示消息
        if (showMessage) {
            message.warning(msg);
        }

        // 延迟跳转，让用户看清提示（静默模式下无需延迟）
        const delay = showMessage ? 1500 : 100;
        setTimeout(() => {
            const current = window.location.pathname + window.location.search;
            const redirect = encodeURIComponent(current);
            const loginPath = `${BASE_PATH}login`.replace(/\/+/g, '/');
            if (!window.location.pathname.startsWith(loginPath)) {
                window.location.href = `${loginPath}?redirect=${redirect}`;
            }
            // 重置标志
            isRelogging = false;
        }, delay);
    }
};

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        // 使用 JWT 认证：从 store 获取 accessToken 并添加到请求头
        try {
            const { accessToken } = useAuthStore.getState();
            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }
        } catch {
            // 忽略错误（store 可能未初始化）
        }

        // 保持 withCredentials 用于 cookie 传递（如有需要）
        config.withCredentials = true;
        return config;
    },
    (error) => Promise.reject(error)
);

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
        const { data, config } = response;
        const requestUrl = config.url;
        const isSilent = isSilentRequest(requestUrl);

        // 检查业务错误码，如果是401认证失败，直接跳转登录
        if (data.restCode === '401' || (data.restCode === 401) || data.message?.includes('认证失败')) {
            // 静默请求不显示错误消息
            handleAuthFailure(data.message || '认证失败，请重新登录', !isSilent);
            return Promise.reject({ response: { data }, isAuthError: true });
        }

        // 检查业务错误码，如果是403权限不足，跳转到权限不足页面
        if (data.restCode === '403' || (data.restCode === 403) || data.message?.includes('权限不足') || data.message?.includes('无权限')) {
            if (typeof window !== 'undefined') {
                message.error('权限不足');
                const noPermissionPath = `${BASE_PATH}no-permission`.replace(/\/+/g, '/');
                if (!window.location.pathname.startsWith(noPermissionPath)) {
                    window.location.href = noPermissionPath;
                }
            }
            return Promise.reject({ response: { data }, isPermissionError: true });
        }

        // 如果API返回了其他错误码
        if (data.restCode !== '200' && data.restCode !== '0' && !data.success) {
            // 抛出包含原始响应体的数据，供调用方自行处理提示
            return Promise.reject({ response: { data } });
        }

        return data;
    },
    (error: AxiosError) => {
        const requestUrl = error.config?.url;
        const isSilent = isSilentRequest(requestUrl);

        // 处理HTTP状态码401
        if (error.response && error.response.status === 401) {
            handleAuthFailure('会话已过期，请重新登录', !isSilent);
        }
        return Promise.reject(error);
    }
);

// 定义一个与拦截器行为一致的类型（直接返回数据T，而不是AxiosResponse<T>）
type CustomAxiosInstance = Omit<import('axios').AxiosInstance, 'get' | 'post' | 'put' | 'delete' | 'patch'> & {
    get<T = any, R = T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<R>;
    post<T = any, R = T>(url: string, data?: any, config?: import('axios').AxiosRequestConfig): Promise<R>;
    put<T = any, R = T>(url: string, data?: any, config?: import('axios').AxiosRequestConfig): Promise<R>;
    delete<T = any, R = T>(url: string, config?: import('axios').AxiosRequestConfig): Promise<R>;
    patch<T = any, R = T>(url: string, data?: any, config?: import('axios').AxiosRequestConfig): Promise<R>;
};

export default request as unknown as CustomAxiosInstance;