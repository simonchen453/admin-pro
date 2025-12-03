import './App.css'
import AppRouter from './router/AppRouter';
import ErrorBoundary from './components/ErrorBoundary';
import { ConfigProvider, App as AntdApp } from 'antd';
import zhCN from 'antd/locale/zh_CN';

import StaticAntd from './components/StaticAntd';

function App() {
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
