import React from 'react';
import { Button, Typography, Space } from 'antd';
import { GithubOutlined, RocketOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';

const { Title, Paragraph } = Typography;

const Hero: React.FC = () => {
    const { t } = useTranslation();

    return (
        <div style={{
            minHeight: '80vh',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            textAlign: 'center',
            padding: '0 2rem',
            background: 'radial-gradient(circle at center, rgba(99, 102, 241, 0.15) 0%, rgba(15, 23, 42, 0) 70%)'
        }}>
            <div className="container">
                <Space direction="vertical" size="large">
                    <div style={{ marginBottom: '1rem' }}>
                        <span style={{
                            background: 'rgba(99, 102, 241, 0.1)',
                            color: '#818cf8',
                            padding: '0.5rem 1rem',
                            borderRadius: '2rem',
                            fontSize: '0.875rem',
                            fontWeight: 600,
                            border: '1px solid rgba(99, 102, 241, 0.2)'
                        }}>
                            {t('hero.badge')}
                        </span>
                    </div>

                    <Title level={1} style={{ fontSize: '4rem', margin: 0, lineHeight: 1.2 }}>
                        {t('hero.title.prefix')} <span className="gradient-text">{t('hero.title.highlight')}</span> {t('hero.title.suffix')}
                    </Title>

                    <Paragraph style={{ fontSize: '1.25rem', color: '#94a3b8', maxWidth: '600px', margin: '0 auto' }}>
                        {t('hero.description')}
                    </Paragraph>

                    <Space size="middle" style={{ marginTop: '2rem' }}>
                        <button className="btn-primary" style={{ fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <RocketOutlined /> {t('hero.demo')}
                        </button>
                        <Button
                            size="large"
                            icon={<GithubOutlined />}
                            style={{
                                height: '48px',
                                padding: '0 2rem',
                                fontSize: '1rem',
                                color: '#f8fafc',
                                borderColor: 'rgba(255, 255, 255, 0.2)'
                            }}
                            ghost
                        >
                            {t('hero.github')}
                        </Button>
                    </Space>
                </Space>
            </div>
        </div>
    );
};

export default Hero;
