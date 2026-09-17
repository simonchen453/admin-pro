import { useEffect, useState } from 'react';
import './App.css'
import AppRouter from './router/AppRouter';
import ErrorBoundary from './components/ErrorBoundary';
import { ConfigProvider, App as AntdApp, Spin } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { useAuthStore } from './stores/useUserStore';
import { antdTheme } from './config/theme';

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
                background: 'var(--ap-bg)'
            }}>
                <Spin size="large" tip="正在初始化..." />
            </div>
        );
    }

    return (
        <ConfigProvider
            locale={zhCN}
            theme={antdTheme}
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
