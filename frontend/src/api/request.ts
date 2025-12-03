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

// 防止重复跳转的标志
let isRelogging = false;

// 统一处理认证失败
const handleAuthFailure = (msg: string = '会话已过期，请重新登录') => {
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
        message.warning(msg);

        // 延迟跳转，让用户看清提示
        setTimeout(() => {
            const current = window.location.pathname + window.location.search;
            const redirect = encodeURIComponent(current);
            if (!window.location.pathname.startsWith('/login')) {
                window.location.href = `/login?redirect=${redirect}`;
            }
            // 重置标志
            isRelogging = false;
        }, 1500);
    }
};

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        // 使用session认证，浏览器会自动发送cookie
        // 设置withCredentials确保cookie被发送
        config.withCredentials = true;
        return config;
    },
    (error) => Promise.reject(error)
);

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
        const { data } = response;

        // 检查业务错误码，如果是401认证失败，直接跳转登录
        if (data.restCode === '401' || (data.restCode === 401) || data.message?.includes('认证失败')) {
            handleAuthFailure(data.message || '认证失败，请重新登录');
            return Promise.reject({ response: { data }, isAuthError: true });
        }

        // 检查业务错误码，如果是403权限不足，跳转到权限不足页面
        if (data.restCode === '403' || (data.restCode === 403) || data.message?.includes('权限不足') || data.message?.includes('无权限')) {
            if (typeof window !== 'undefined') {
                message.error('权限不足');
                if (!window.location.pathname.startsWith('/no-permission')) {
                    window.location.href = '/no-permission';
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
        // 处理HTTP状态码401
        if (error.response && error.response.status === 401) {
            handleAuthFailure('会话已过期，请重新登录');
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