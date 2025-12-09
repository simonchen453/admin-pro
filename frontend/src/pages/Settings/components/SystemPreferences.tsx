import React, { useState } from 'react';
import { Card, Typography, Row, Col, Select, Space } from 'antd';
import { CheckCircleFilled, DesktopOutlined, BorderOuterOutlined, BugOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

const SystemPreferences: React.FC = () => {
    const [theme, setTheme] = useState('light');
    const [language, setLanguage] = useState('zh-CN');

    const themes = [
        { key: 'light', name: '浅色模式', color: '#ffffff', borderColor: '#d9d9d9' },
        { key: 'dark', name: '深色模式', color: '#1f1f1f', borderColor: '#434343' },
        { key: 'system', name: '跟随系统', color: 'linear-gradient(135deg, #ffffff 50%, #1f1f1f 50%)', borderColor: '#d9d9d9' },
    ];

    return (
        <div>
            <div style={{ marginBottom: 32 }}>
                <Title level={5} style={{ marginBottom: 8 }}>界面主题</Title>
                <Text type="secondary">自定义界面外观与显示语言。</Text>
            </div>

            <Row gutter={[24, 24]}>
                {themes.map((item) => (
                    <Col key={item.key} xs={24} sm={8}>
                        <div
                            onClick={() => setTheme(item.key)}
                            style={{
                                cursor: 'pointer',
                                borderRadius: 8,
                                border: `2px solid ${theme === item.key ? '#5b73e8' : 'transparent'}`,
                                padding: 4,
                                position: 'relative'
                            }}
                        >
                            <div
                                style={{
                                    height: 120,
                                    background: item.key === 'system' ? item.color : item.color,
                                    borderRadius: 6,
                                    border: `1px solid ${item.borderColor}`,
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    boxShadow: '0 2px 8px rgba(0,0,0,0.05)',
                                    position: 'relative',
                                    overflow: 'hidden'
                                }}
                            >
                                {/* Simple Preview UI */}
                                {item.key === 'light' && (
                                    <div style={{ width: '80%', height: '60%', background: '#f5f5f5', borderRadius: 4, display: 'flex' }}>
                                        <div style={{ width: '30%', height: '100%', background: '#fff', borderRight: '1px solid #eee' }}></div>
                                        <div style={{ width: '70%', height: '100%', padding: 4 }}>
                                            <div style={{ width: '100%', height: 8, background: '#fff', marginBottom: 4 }}></div>
                                            <div style={{ width: '60%', height: 8, background: '#fff' }}></div>
                                        </div>
                                    </div>
                                )}
                                {item.key === 'dark' && (
                                    <div style={{ width: '80%', height: '60%', background: '#141414', borderRadius: 4, display: 'flex' }}>
                                        <div style={{ width: '30%', height: '100%', background: '#1f1f1f', borderRight: '1px solid #303030' }}></div>
                                        <div style={{ width: '70%', height: '100%', padding: 4 }}>
                                            <div style={{ width: '100%', height: 8, background: '#1f1f1f', marginBottom: 4 }}></div>
                                            <div style={{ width: '60%', height: 8, background: '#1f1f1f' }}></div>
                                        </div>
                                    </div>
                                )}
                                {item.key === 'system' && (
                                    <DesktopOutlined style={{ fontSize: 32, color: '#8c8c8c' }} />
                                )}

                                {theme === item.key && (
                                    <CheckCircleFilled style={{ position: 'absolute', top: 8, right: 8, color: '#5b73e8', fontSize: 20 }} />
                                )}
                            </div>
                            <div style={{ textAlign: 'center', marginTop: 8 }}>
                                <Text style={{ color: theme === item.key ? '#5b73e8' : undefined }}>{item.name}</Text>
                            </div>
                        </div>
                    </Col>
                ))}
            </Row>

            <div style={{ marginTop: 40, maxWidth: 400 }}>
                <Title level={5} style={{ marginBottom: 16 }}>显示语言</Title>
                <Select
                    size="large"
                    value={language}
                    onChange={setLanguage}
                    style={{ width: '100%' }}
                    options={[
                        { value: 'zh-CN', label: '简体中文 (Chinese Simplified)' },
                        { value: 'en-US', label: 'English (United States)' },
                        { value: 'ja-JP', label: '日本語 (Japanese)' },
                    ]}
                />
            </div>
        </div>
    );
};

export default SystemPreferences;
