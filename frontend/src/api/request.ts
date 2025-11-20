import axios, { AxiosError, type AxiosResponse } from 'axios';
import { useAuthStore } from '../stores/useUserStore';

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE || '/api',
    timeout: 10000,
    withCredentials: true,
    headers: {
        'X-Requested-With': 'XMLHttpRequest',
    },
});

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
            // 清除本地认证状态并跳转登录
            try {
                const { clearAuth } = useAuthStore.getState();
                clearAuth();
            } catch {
                // 忽略错误，继续执行
            }
            
            if (typeof window !== 'undefined') {
                // 显示认证失败提示
                console.warn('认证失败，正在跳转到登录页面...', data.message);
                
                const current = window.location.pathname + window.location.search;
                const redirect = encodeURIComponent(current);
                if (!window.location.pathname.startsWith('/login')) {
                    window.location.href = `/login?redirect=${redirect}`;
                }
            }
            return Promise.reject({ response: { data }, isAuthError: true });
        }
        
        // 检查业务错误码，如果是403权限不足，跳转到权限不足页面
        if (data.restCode === '403' || (data.restCode === 403) || data.message?.includes('权限不足') || data.message?.includes('无权限')) {
            if (typeof window !== 'undefined') {
                // 显示权限不足提示
                console.warn('权限不足，正在跳转到权限不足页面...', data.message);
                
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
            // session过期时，清除本地认证状态并跳转登录
            try {
                const { clearAuth } = useAuthStore.getState();
                clearAuth();
            } catch {
                // 忽略错误，继续执行
            }
            if (typeof window !== 'undefined') {
                // 显示认证失败提示
                console.warn('Session过期，正在跳转到登录页面...');
                
                const current = window.location.pathname + window.location.search;
                const redirect = encodeURIComponent(current);
                if (!window.location.pathname.startsWith('/login')) {
                    window.location.href = `/login?redirect=${redirect}`;
                }
            }
        }
        return Promise.reject(error);
    }
);

export default request;