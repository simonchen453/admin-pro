import { useState } from 'react';
import { Card, Tabs, Space, Breadcrumb, Button, Divider } from 'antd';
import { 
  SafetyOutlined, 
  UserOutlined,
  HomeOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import ChangePasswordForm from './components/ChangePasswordForm';
import ProfileView from './components/ProfileView';
import ProfileEditForm from './components/ProfileEditForm';
import './Settings.css';

function Settings() {
  const navigate = useNavigate();
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
          <ChangePasswordForm />
        </div>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      {/* 面包屑导航 */}
      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center' }}>
        <Breadcrumb
          items={[
            {
              title: (
                <Button
                  type="link"
                  icon={<HomeOutlined />}
                  onClick={() => navigate('/')}
                  style={{ padding: 0, height: 'auto', lineHeight: 1 }}
                >
                  首页
                </Button>
              )
            },
            {
              title: '个人资料'
            }
          ]}
        />
      </div>
      
      <Divider />

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

