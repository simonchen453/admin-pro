import { useState, useEffect } from 'react';
import { Descriptions, Avatar, Tag, Spin, message, Button } from 'antd';
import { UserOutlined, EditOutlined } from '@ant-design/icons';
import { getCurrentUserInfoApi } from '../../../api/auth';
import type { UserEntity } from '../../../types';
import dayjs from 'dayjs';

interface ProfileViewProps {
  onEdit?: () => void;
  refreshTrigger?: number;
}

function ProfileView({ onEdit, refreshTrigger }: ProfileViewProps) {
  const [loading, setLoading] = useState(true);
  const [userInfo, setUserInfo] = useState<UserEntity | null>(null);

  useEffect(() => {
    loadUserInfo();
  }, [refreshTrigger]);

  const loadUserInfo = async () => {
    try {
      setLoading(true);
      const user = await getCurrentUserInfoApi();
      setUserInfo(user as unknown as UserEntity);
    } catch (error: any) {
      console.error('获取用户信息失败:', error);
      message.error(error?.message || '获取用户信息失败');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr?: string) => {
    if (!dateStr) return '-';
    return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss');
  };

  const getStatusTag = (status?: string) => {
    if (status === 'ACTIVE' || status === 'active') {
      return <Tag color="success">激活</Tag>;
    } else if (status === 'INACTIVE' || status === 'inactive') {
      return <Tag color="default">停用</Tag>;
    }
    return <Tag>{status || '-'}</Tag>;
  };

  const getSexText = (sex?: string) => {
    if (sex === 'M' || sex === 'male' || sex === '男') return '男';
    if (sex === 'F' || sex === 'female' || sex === '女') return '女';
    return sex || '-';
  };

  return (
    <Spin spinning={loading}>
      {userInfo && (
        <div>
          {onEdit && (
            <div style={{ marginBottom: 16, textAlign: 'right' }}>
              <Button 
                type="primary" 
                icon={<EditOutlined />}
                onClick={onEdit}
              >
                编辑
              </Button>
            </div>
          )}
          <Descriptions column={2} bordered>
            <Descriptions.Item label="头像" span={2}>
              <Avatar
                size={80}
                src={userInfo.avatarUrl}
                icon={<UserOutlined />}
              />
            </Descriptions.Item>
            <Descriptions.Item label="登录名">
              {userInfo.loginName || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="真实姓名">
              {userInfo.realName || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="用户域">
              {userInfo.userDomain || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="手机号码">
              {userInfo.mobileNo || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="邮箱">
              {userInfo.email || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="性别">
              {getSexText(userInfo.sex)}
            </Descriptions.Item>
            <Descriptions.Item label="状态">
              {getStatusTag(userInfo.status)}
            </Descriptions.Item>
            <Descriptions.Item label="部门">
              {userInfo.deptName || userInfo.deptNo || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="最后登录时间" span={2}>
              {formatDate(userInfo.latestLoginTime)}
            </Descriptions.Item>
          </Descriptions>
        </div>
      )}
    </Spin>
  );
}

export default ProfileView;

