import React, { useState } from 'react';
import { Card, Menu, Typography, theme } from 'antd';
import {
  UserOutlined,
  SafetyOutlined,
  BellOutlined,
  SkinOutlined
} from '@ant-design/icons';
import ProfileEditForm from './components/ProfileEditForm';
import ChangePasswordForm from './components/ChangePasswordForm';
import NotificationSettings from './components/NotificationSettings';
import SystemPreferences from './components/SystemPreferences';
import './Settings.css';

const { Title, Text } = Typography;

const Settings: React.FC = () => {
  const [selectedKey, setSelectedKey] = useState('profile');
  const { token } = theme.useToken();

  const menuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料',
    },
    {
      key: 'security',
      icon: <SafetyOutlined />,
      label: '账号安全',
    },
    {
      key: 'notification',
      icon: <BellOutlined />,
      label: '消息通知',
    },
    {
      key: 'preferences',
      icon: <SkinOutlined />,
      label: '系统偏好',
    },
  ];

  const renderContent = () => {
    switch (selectedKey) {
      case 'profile':
        return (
          <div>
            <Title level={4} style={{ marginBottom: 24, marginTop: 0 }}>个人资料</Title>
            <ProfileEditForm />
          </div>
        );
      case 'security':
        return (
          <div>
            <Title level={4} style={{ marginBottom: 8, marginTop: 0 }}>账号安全</Title>
            <Text type="secondary" style={{ display: 'block', marginBottom: 24 }}>管理您的登录密码及双重验证设置。</Text>
            <ChangePasswordForm />
          </div>
        );
      case 'notification':
        return (
          <div>
            <Title level={4} style={{ marginBottom: 24, marginTop: 0 }}>消息通知</Title>
            <NotificationSettings />
          </div>
        );
      case 'preferences':
        return (
          <div>
            <Title level={4} style={{ marginBottom: 24, marginTop: 0 }}>系统偏好</Title>
            <SystemPreferences />
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="fade-in" style={{ padding: '24px', display: 'flex', gap: '24px', alignItems: 'flex-start' }}>

      {/* Left Sidebar */}
      <Card
        className="settings-sidebar modern-card"
        style={{ width: 280, flexShrink: 0 }}
        styles={{ body: { padding: '12px' } }}
        bordered={false}
      >
        <div style={{ padding: '12px 16px 24px 16px' }}>
          <Title level={4} style={{ margin: 0 }}>设置中心</Title>
          <Text type="secondary" style={{ fontSize: '12px' }}>管理您的账号与系统偏好</Text>
        </div>

        <Menu
          mode="vertical"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => setSelectedKey(key)}
          style={{ borderRight: 'none' }}
          className="settings-menu"
          theme="light"
        />
      </Card>

      {/* Right Content */}
      <Card
        className="settings-content modern-card"
        style={{ flex: 1, minHeight: 600 }}
        styles={{ body: { padding: '32px' } }}
        bordered={false}
      >
        {renderContent()}
      </Card>
    </div>
  );
};

export default Settings;
