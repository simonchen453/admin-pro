import React from 'react';
import { Typography, Space } from 'antd';
import { GithubOutlined, TwitterOutlined, LinkedinOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';

const { Text, Link } = Typography;

const Footer: React.FC = () => {
    const { t } = useTranslation();

    return (
        <div style={{ padding: '3rem 0', background: '#0b1120', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
            <div className="container">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '2rem' }}>
                    <div>
                        <Text style={{ fontSize: '1.5rem', fontWeight: 700, color: '#f8fafc' }}>
                            Admin<span style={{ color: '#6366f1' }}>Pro</span>
                        </Text>
                        <br />
                        <Text type="secondary">
                            {t('footer.copyright')}
                        </Text>
                    </div>

                    <Space size="large">
                        <Link href="#" style={{ color: '#94a3b8' }}><GithubOutlined style={{ fontSize: '1.5rem' }} /></Link>
                        <Link href="#" style={{ color: '#94a3b8' }}><TwitterOutlined style={{ fontSize: '1.5rem' }} /></Link>
                        <Link href="#" style={{ color: '#94a3b8' }}><LinkedinOutlined style={{ fontSize: '1.5rem' }} /></Link>
                    </Space>
                </div>
            </div>
        </div>
    );
};

export default Footer;
