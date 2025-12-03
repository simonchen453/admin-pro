import React from 'react';
import { Typography, Row, Col } from 'antd';
import {
    SafetyCertificateOutlined,
    CodeOutlined,
    CloudServerOutlined,
    DashboardOutlined,
    GlobalOutlined,
    MobileOutlined
} from '@ant-design/icons';
import { useTranslation, Trans } from 'react-i18next';

const { Title, Paragraph } = Typography;

const Features: React.FC = () => {
    const { t } = useTranslation();

    const features = [
        {
            icon: <SafetyCertificateOutlined style={{ fontSize: '2rem', color: '#6366f1' }} />,
            title: t('features.rbac.title'),
            description: t('features.rbac.desc')
        },
        {
            icon: <CodeOutlined style={{ fontSize: '2rem', color: '#8b5cf6' }} />,
            title: t('features.codegen.title'),
            description: t('features.codegen.desc')
        },
        {
            icon: <CloudServerOutlined style={{ fontSize: '2rem', color: '#ec4899' }} />,
            title: t('features.docker.title'),
            description: t('features.docker.desc')
        },
        {
            icon: <DashboardOutlined style={{ fontSize: '2rem', color: '#10b981' }} />,
            title: t('features.monitor.title'),
            description: t('features.monitor.desc')
        },
        {
            icon: <GlobalOutlined style={{ fontSize: '2rem', color: '#3b82f6' }} />,
            title: t('features.i18n.title'),
            description: t('features.i18n.desc')
        },
        {
            icon: <MobileOutlined style={{ fontSize: '2rem', color: '#f59e0b' }} />,
            title: t('features.responsive.title'),
            description: t('features.responsive.desc')
        }
    ];

    return (
        <div style={{ padding: '5rem 0', background: '#0f172a' }}>
            <div className="container">
                <div style={{ textAlign: 'center', marginBottom: '4rem' }}>
                    <Title level={2} style={{ fontSize: '2.5rem' }}>
                        <Trans i18nKey="features.title">
                            Packed with <span className="gradient-text">Awesome</span> Features
                        </Trans>
                    </Title>
                    <Paragraph style={{ fontSize: '1.125rem', color: '#94a3b8' }}>
                        {t('features.subtitle')}
                    </Paragraph>
                </div>

                <Row gutter={[24, 24]}>
                    {features.map((feature, index) => (
                        <Col xs={24} sm={12} md={8} key={index}>
                            <div className="glass-card" style={{ height: '100%', transition: 'transform 0.3s' }}>
                                <div style={{ marginBottom: '1.5rem' }}>
                                    {feature.icon}
                                </div>
                                <Title level={4} style={{ marginBottom: '1rem' }}>{feature.title}</Title>
                                <Paragraph style={{ color: '#94a3b8', marginBottom: 0 }}>
                                    {feature.description}
                                </Paragraph>
                            </div>
                        </Col>
                    ))}
                </Row>
            </div>
        </div>
    );
};

export default Features;
