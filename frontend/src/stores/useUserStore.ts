import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { User, LoginRequest } from '../types/index';
import { loginApi, logoutApi } from '../api/auth';
import request from '../api/request';

/**
 * 认证状态接口
 * 
 * 安全策略：
 * - accessToken: 只存在内存中，页面刷新后丢失
 * - refreshToken: 存储在 HttpOnly Cookie 中，前端无法访问
 * - 页面刷新时：调用 /refresh 接口用 Cookie 中的 refreshToken 获取新的 accessToken
 */
interface AuthState {
    isAuthenticated: boolean;
    isInitialized: boolean;  // 应用初始化状态
    currentUser: User | null;
    accessToken: string | null;  // 只在内存中，不持久化
    login: (loginData: LoginRequest) => Promise<void>;
    logout: () => Promise<void>;
    clearAuth: () => void;
    updateCurrentUser: (user: User) => void;
    getAccessToken: () => string | null;
    setAccessToken: (token: string | null) => void;
    initAuth: () => Promise<boolean>;  // 页面加载时初始化认证状态
}

interface UserState {
    users: User[];
    loading: boolean;
    error: string | null;
    addUser: (user: User) => void;
    removeUser: (id: string) => void;
    setUsers: (users: User[]) => void;
    setLoading: (loading: boolean) => void;
    setError: (error: string | null) => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set, get) => ({
            isAuthenticated: false,
            isInitialized: false,
            currentUser: null,
            accessToken: null,  // 内存中的 accessToken

            login: async (loginData: LoginRequest) => {
                try {
                    const response = await loginApi(loginData);
                    // JWT 登录响应包含 accessToken（refreshToken 在 HttpOnly Cookie 中）
                    const tokenData = (response as any).data || response;

                    // Map LoginResponse to User object
                    const userInfo = tokenData.user || tokenData;
                    const user: User = {
                        loginName: userInfo.loginName || userInfo.userId,
                        realName: userInfo.realName,
                        name: userInfo.realName || userInfo.loginName || userInfo.userId,
                        avatarUrl: userInfo.avatarUrl,
                        mobileNo: userInfo.mobileNo,
                        userDomain: userInfo.userDomain || (response as any).domain,
                        id: userInfo.id || (response as any).id,
                        status: 'active'
                    };

                    // accessToken 只存在内存中
                    const accessToken = tokenData.accessToken || null;
                    // refreshToken 在 HttpOnly Cookie 中，前端不需要存储

                    set({
                        isAuthenticated: true,
                        isInitialized: true,
                        currentUser: user,
                        accessToken
                    });
                } catch (error) {
                    console.error('登录失败:', error);
                    throw error;
                }
            },

            logout: async () => {
                try {
                    await logoutApi();
                } catch (error) {
                    console.error('登出失败:', error);
                } finally {
                    // 无论登出API是否成功，都清除本地状态
                    set({
                        isAuthenticated: false,
                        currentUser: null,
                        accessToken: null
                    });
                }
            },

            clearAuth: () => {
                set({
                    isAuthenticated: false,
                    currentUser: null,
                    accessToken: null
                });
            },

            updateCurrentUser: (user: User) => {
                set({ currentUser: user });
            },

            getAccessToken: () => {
                return get().accessToken;
            },

            setAccessToken: (token: string | null) => {
                set({ accessToken: token });
            },

            /**
             * 页面加载时初始化认证状态
             * 使用 HttpOnly Cookie 中的 refreshToken 换取新的 accessToken
             */
            initAuth: async () => {
                try {
                    // 调用 refresh 接口，refreshToken 会自动通过 Cookie 发送
                    const response = await request.post<any>('/api/v1/auth/refresh');

                    if (response.success && response.data) {
                        const tokenData = response.data;
                        const userInfo = tokenData.user || {};

                        const user: User = {
                            loginName: userInfo.loginName || userInfo.userId,
                            realName: userInfo.realName,
                            name: userInfo.realName || userInfo.loginName || userInfo.userId,
                            avatarUrl: userInfo.avatarUrl,
                            mobileNo: userInfo.mobileNo,
                            userDomain: userInfo.userDomain,
                            id: userInfo.id,
                            status: 'active'
                        };

                        set({
                            isAuthenticated: true,
                            isInitialized: true,
                            currentUser: user,
                            accessToken: tokenData.accessToken
                        });

                        return true;
                    } else {
                        set({ isInitialized: true, isAuthenticated: false });
                        return false;
                    }
                } catch (error) {
                    console.log('Token refresh failed, user needs to login');
                    set({
                        isInitialized: true,
                        isAuthenticated: false,
                        accessToken: null,
                        currentUser: null
                    });
                    return false;
                }
            }
        }),
        {
            name: 'auth-storage',
            storage: createJSONStorage(() => localStorage),
            // 只持久化用户信息，不持久化 token（安全要求）
            partialize: (state) => ({
                currentUser: state.currentUser,
                isAuthenticated: state.isAuthenticated,
                // accessToken 和 refreshToken 不持久化到 localStorage
            }),
        }
    )
);

export const useUserStore = create<UserState>((set) => ({
    users: [],
    loading: false,
    error: null,
    setUsers: (users) => set({ users, error: null }),
    addUser: (user) =>
        set((state) => ({ users: [...state.users, user] })),
    removeUser: (id) =>
        set((state) => ({ users: state.users.filter(u => u.id !== id) })),
    setLoading: (loading) => set({ loading }),
    setError: (error) => set({ error }),
}));