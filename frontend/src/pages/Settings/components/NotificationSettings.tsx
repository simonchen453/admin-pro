import React from 'react';
import { Typography, Button, Space } from 'antd';
import { BellOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

const NotificationSettings: React.FC = () => {
    return (
        <div style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: '400px',
            textAlign: 'center'
        }}>
            <div style={{
                width: 80,
                height: 80,
                background: '#eff4ff',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: 24,
                color: '#5b73e8'
            }}>
                <BellOutlined style={{ fontSize: 40 }} />
            </div>

            <Title level={4} style={{ marginBottom: 12 }}>通知中心升级中</Title>

            <Text type="secondary" style={{ maxWidth: 400, marginBottom: 24, display: 'block' }}>
                我们正在重构通知推送服务，以支持更精细化的消息订阅管理。该功能预计将在下一个版本中上线。
            </Text>

            <Button size="large">了解更多</Button>
        </div>
    );
};

export default NotificationSettings;
