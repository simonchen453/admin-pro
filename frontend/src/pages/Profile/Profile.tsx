import React, { useState, useEffect } from 'react';
import {
  Card,
  Descriptions,
  Avatar,
  Tag,
  Space,
  Button,
  Spin,
  message,
  Row,
  Col,
  Tabs,
  Badge,
} from 'antd';
import {
  UserOutlined,
  KeyOutlined,
  PhoneOutlined,
  MailOutlined,
  ApartmentOutlined,
  GlobalOutlined,
  ClockCircleOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getCurrentUserInfoApi } from '../../api/auth';
import type { UserEntity } from '../../types';
import dayjs from 'dayjs';
import './Profile.css';

function Profile() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [userInfo, setUserInfo] = useState<UserEntity | null>(null);

  useEffect(() => {
    loadUserInfo();
  }, []);

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

  const getSexText = (sex?: string) => {
    if (sex === 'M' || sex === 'male' || sex === '男') return '男';
    if (sex === 'F' || sex === 'female' || sex === '女') return '女';
    return sex || '-';
  };

  const displayName = userInfo?.realName || userInfo?.loginName || '管理员';
  const initial = displayName.charAt(0).toUpperCase();

  return (
    <div className="profile-container fade-in">
      <Spin spinning={loading}>
        {userInfo && (
          <Row gutter={[20, 20]}>
            {/* 左侧个人名片卡 */}
            <Col xs={24} md={8}>
              <Card bordered={false} className="profile-card">
                <div className="profile-user-summary">
                  <div className="profile-avatar-box">
                    <Avatar
                      size={88}
                      src={userInfo.avatarUrl}
                      className="profile-avatar"
                      style={{ backgroundColor: '#1677ff' }}
                    >
                      {initial}
                    </Avatar>
                  </div>
                  <div className="profile-name">{displayName}</div>
                  <div className="profile-account">@{userInfo.loginName}</div>
                  <div style={{ marginTop: 12 }}>
                    <Tag color="purple" icon={<SafetyCertificateOutlined />}>
                      超级系统管理员
                    </Tag>
                  </div>
                </div>

                <div className="profile-contact-list">
                  <div className="profile-contact-item">
                    <span className="profile-contact-label">
                      <UserOutlined /> 用户标识 ID
                    </span>
                    <span className="profile-contact-val">{userInfo.id || '-'}</span>
                  </div>
                  <div className="profile-contact-item">
                    <span className="profile-contact-label">
                      <GlobalOutlined /> 所属域
                    </span>
                    <span className="profile-contact-val">
                      <Tag color="blue">{userInfo.userDomain || 'Default'}</Tag>
                    </span>
                  </div>
                  <div className="profile-contact-item">
                    <span className="profile-contact-label">
                      <PhoneOutlined /> 手机号码
                    </span>
                    <span className="profile-contact-val">
                      {userInfo.mobileNo || '未绑定'}
                    </span>
                  </div>
                  <div className="profile-contact-item">
                    <span className="profile-contact-label">
                      <MailOutlined /> 电子邮箱
                    </span>
                    <span className="profile-contact-val">
                      {userInfo.email || '未绑定'}
                    </span>
                  </div>
                  <div className="profile-contact-item">
                    <span className="profile-contact-label">
                      <ApartmentOutlined /> 所属部门编号
                    </span>
                    <span className="profile-contact-val">
                      {userInfo.deptNo || '-'}
                    </span>
                  </div>
                  <div className="profile-contact-item">
                    <span className="profile-contact-label">账号状态</span>
                    <Badge status="success" text="正常活跃" />
                  </div>
                </div>
              </Card>
            </Col>

            {/* 右侧详细资料与操作 Tabs */}
            <Col xs={24} md={16}>
              <Card bordered={false} className="profile-card">
                <Tabs
                  defaultActiveKey="info"
                  items={[
                    {
                      key: 'info',
                      label: '基本资料详情',
                      children: (
                        <div style={{ paddingTop: 8 }}>
                          <Descriptions column={2} bordered size="middle">
                            <Descriptions.Item label="登录名">
                              <strong>{userInfo.loginName || '-'}</strong>
                            </Descriptions.Item>
                            <Descriptions.Item label="真实姓名">
                              {userInfo.realName || '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="用户域">
                              <Tag color="geekblue">{userInfo.userDomain || '-'}</Tag>
                            </Descriptions.Item>
                            <Descriptions.Item label="性别">
                              {getSexText(userInfo.sex)}
                            </Descriptions.Item>
                            <Descriptions.Item label="手机号码">
                              {userInfo.mobileNo || '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="电子邮箱">
                              {userInfo.email || '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="部门编号">
                              {userInfo.deptNo || '-'}
                            </Descriptions.Item>
                            <Descriptions.Item label="账号状态">
                              <Badge status="success" text="已激活" />
                            </Descriptions.Item>
                            <Descriptions.Item label="最后登录时间" span={2}>
                              <Space>
                                <ClockCircleOutlined style={{ color: 'var(--ap-text-3)' }} />
                                <span>{formatDate(userInfo.latestLoginTime)}</span>
                              </Space>
                            </Descriptions.Item>
                            <Descriptions.Item label="用户备注" span={2}>
                              {userInfo.description || '暂无备注说明'}
                            </Descriptions.Item>
                          </Descriptions>
                        </div>
                      ),
                    },
                    {
                      key: 'security',
                      label: '账号与安全设置',
                      children: (
                        <div style={{ padding: '16px 0', maxWidth: 480 }}>
                          <p style={{ color: 'var(--ap-text-2)', marginBottom: 20 }}>
                            定期更改登录密码有助于提高账号安全性，密码应包含字母与数字组合。
                          </p>
                          <Button
                            type="primary"
                            icon={<KeyOutlined />}
                            onClick={() => navigate('/changepwd')}
                          >
                            前往修改登录密码
                          </Button>
                        </div>
                      ),
                    },
                  ]}
                />
              </Card>
            </Col>
          </Row>
        )}
      </Spin>
    </div>
  );
}

export default Profile;
