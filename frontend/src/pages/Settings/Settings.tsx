import { useState } from 'react';
import { Card, Tabs, Space, Typography, Divider } from 'antd';
import { 
  SafetyOutlined, 
  UserOutlined
} from '@ant-design/icons';
import ChangePasswordForm from './components/ChangePasswordForm';
import ProfileView from './components/ProfileView';
import ProfileEditForm from './components/ProfileEditForm';
import './Settings.css';

const { Title } = Typography;

function Settings() {
  const [activeTab, setActiveTab] = useState('profile');
  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [profileRefreshKey, setProfileRefreshKey] = useState(0);

  const tabItems = [
    {
      key: 'profile',
      label: (
        <Space>
          <UserOutlined />
          <span>个人资料</span>
        </Space>
      ),
      children: (
        <div className="settings-tab-content">
          <Title level={4}>个人资料</Title>
          <Divider />
          {isEditingProfile ? (
            <ProfileEditForm
              onSuccess={() => {
                setIsEditingProfile(false);
                setProfileRefreshKey(prev => prev + 1);
              }}
              onCancel={() => {
                setIsEditingProfile(false);
              }}
            />
          ) : (
            <ProfileView
              onEdit={() => {
                setIsEditingProfile(true);
              }}
              refreshTrigger={profileRefreshKey}
            />
          )}
        </div>
      ),
    },
    {
      key: 'security',
      label: (
        <Space>
          <SafetyOutlined />
          <span>账户安全</span>
        </Space>
      ),
      children: (
        <div className="settings-tab-content">
          <Title level={4}>账户安全</Title>
          <Divider />
          <ChangePasswordForm />
        </div>
      ),
    },
  ];

  return (
    <div className="settings-container">
      <Card>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabItems}
          size="large"
        />
      </Card>
    </div>
  );
}

export default Settings;

