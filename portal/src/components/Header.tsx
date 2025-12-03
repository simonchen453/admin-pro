import React from 'react';
import { Space, Typography } from 'antd';
import LanguageSwitcher from './LanguageSwitcher';

const { Text } = Typography;

const Header: React.FC = () => {
    return (
        <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            zIndex: 1000,
            padding: '1rem 2rem',
            background: 'rgba(15, 23, 42, 0.6)',
            backdropFilter: 'blur(12px)',
            borderBottom: '1px solid rgba(255, 255, 255, 0.05)',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
        }}>
            <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                <Space size="middle" align="center" style={{ cursor: 'pointer' }}>
                    <div style={{
                        width: '40px',
                        height: '40px',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                    }}>
                        <img src="/favicon.svg" alt="Logo" style={{ width: '100%', height: '100%' }} />
                    </div>
                    <Text style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f8fafc', letterSpacing: '-0.5px' }}>
                        Admin<span style={{ color: '#6366f1' }}>Pro</span>
                    </Text>
                </Space>

                <div style={{ position: 'relative', top: 'auto', right: 'auto' }}>
                    <LanguageSwitcher />
                </div>
            </div>
        </div>
    );
};

export default Header;
