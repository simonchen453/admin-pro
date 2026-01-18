import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User, LoginRequest } from '../types/index';
import { loginApi, logoutApi } from '../api/auth';

interface AuthState {
    isAuthenticated: boolean;
    currentUser: User | null;
    accessToken: string | null;
    refreshToken: string | null;
    login: (loginData: LoginRequest) => Promise<void>;
    logout: () => Promise<void>;
    clearAuth: () => void;
    updateCurrentUser: (user: User) => void;
    getAccessToken: () => string | null;
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
            currentUser: null,
            accessToken: null,
            refreshToken: null,
            login: async (loginData: LoginRequest) => {
                try {
                    const response = await loginApi(loginData);
                    // JWT 登录响应包含 accessToken 和 refreshToken
                    // 检查响应结构（可能是直接返回或在 data 字段中）
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

                    // 存储 JWT Tokens
                    const accessToken = tokenData.accessToken || null;
                    const refreshToken = tokenData.refreshToken || null;

                    set({
                        isAuthenticated: true,
                        currentUser: user,
                        accessToken,
                        refreshToken
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
                        accessToken: null,
                        refreshToken: null
                    });
                }
            },
            clearAuth: () => {
                set({
                    isAuthenticated: false,
                    currentUser: null,
                    accessToken: null,
                    refreshToken: null
                });
            },
            updateCurrentUser: (user: User) => {
                set({ currentUser: user });
            },
            getAccessToken: () => {
                return get().accessToken;
            }
        }),
        {
            name: 'auth-storage',
            partialize: (state) => ({
                currentUser: state.currentUser,
                isAuthenticated: state.isAuthenticated,
                accessToken: state.accessToken,
                refreshToken: state.refreshToken
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