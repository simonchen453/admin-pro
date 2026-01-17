import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User, LoginRequest } from '../types/index';
import { loginApi, logoutApi } from '../api/auth';

interface AuthState {
    isAuthenticated: boolean;
    currentUser: User | null;
    login: (loginData: LoginRequest) => Promise<void>;
    logout: () => Promise<void>;
    clearAuth: () => void;
    updateCurrentUser: (user: User) => void;
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
        (set) => ({
            isAuthenticated: false,
            currentUser: null,
            login: async (loginData: LoginRequest) => {
                try {
                    const response = await loginApi(loginData);
                    // Map LoginResponse to User object
                    const user: User = {
                        loginName: response.userId,
                        realName: response.realName,
                        name: response.realName || response.userId,
                        avatarUrl: response.avatarUrl,
                        mobileNo: response.mobileNo,
                        userDomain: response.domain,
                        id: response.id,
                        status: 'active'
                    };

                    // session认证不需要存储token，后端会设置session cookie
                    set({
                        isAuthenticated: true,
                        currentUser: user
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
                        currentUser: null
                    });
                }
            },
            clearAuth: () => {
                set({
                    isAuthenticated: false,
                    currentUser: null
                });
            },
            updateCurrentUser: (user: User) => {
                set({ currentUser: user });
            }
        }),
        {
            name: 'auth-storage',
            partialize: (state) => ({
                currentUser: state.currentUser,
                isAuthenticated: state.isAuthenticated
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