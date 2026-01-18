import { useEffect, useState } from 'react';
import './App.css'
import AppRouter from './router/AppRouter';
import ErrorBoundary from './components/ErrorBoundary';
import { ConfigProvider, App as AntdApp, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { useAuthStore } from './stores/useUserStore';

import StaticAntd from './components/StaticAntd';

function App() {
    const [isLoading, setIsLoading] = useState(true);
    const initAuth = useAuthStore(state => state.initAuth);
    const isInitialized = useAuthStore(state => state.isInitialized);

    // 页面加载时，尝试用 refreshToken Cookie 恢复认证状态
    useEffect(() => {
        const init = async () => {
            try {
                await initAuth();
            } catch (error) {
                console.log('Auth initialization failed:', error);
            } finally {
                setIsLoading(false);
            }
        };

        // 只有在还未初始化时才调用
        if (!isInitialized) {
            init();
        } else {
            setIsLoading(false);
        }
    }, [initAuth, isInitialized]);

    // 显示加载状态
    if (isLoading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
                background: '#f3f4f6'
            }}>
                <Spin size="large" tip="正在初始化..." />
            </div>
        );
    }

    return (
        <ConfigProvider
            locale={zhCN}
            theme={{
                token: {
                    colorPrimary: '#6366f1', // Indigo
                    borderRadius: 8,
                    fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
                    colorBgContainer: '#ffffff',
                },
                components: {
                    Button: {
                        controlHeight: 36,
                        boxShadow: '0 2px 0 rgba(99, 102, 241, 0.1)',
                    },
                    Card: {
                        boxShadowTertiary: '0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px 0 rgba(0, 0, 0, 0.02)',
                    },
                    Input: {
                        controlHeight: 36,
                    },
                    Select: {
                        controlHeight: 36,
                    },
                    DatePicker: {
                        controlHeight: 36,
                    },
                    Layout: {
                        bodyBg: '#f3f4f6', // Very light gray for background
                        headerBg: '#ffffff',
                    }
                }
            }}
        >
            <AntdApp>
                <StaticAntd />
                <ErrorBoundary>
                    <AppRouter />
                </ErrorBoundary>
            </AntdApp>
        </ConfigProvider>
    );
}

export default App
