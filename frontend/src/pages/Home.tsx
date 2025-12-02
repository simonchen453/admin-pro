import React, { useState, useEffect, useCallback } from 'react';
import { Card, Row, Col, Statistic, Button, List, Avatar, Tag, Space, Typography, Empty, Spin, Descriptions, Tooltip } from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  ApartmentOutlined,
  WifiOutlined,
  SettingOutlined,
  MenuOutlined,
  ToolOutlined,
  FileTextOutlined,
  ClockCircleOutlined,
  DatabaseOutlined,
  CodeOutlined,
  SafetyOutlined,
  ThunderboltOutlined,
  BarChartOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getSystemInfoApi, getStatisticsApi, getRecentActivitiesApi, type RecentActivity as ApiRecentActivity } from '../api/common';
import type { SystemInfo } from '../types';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';
import './Home.css';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const { Title, Text } = Typography;

interface StatisticCard {
  title: string;
  value: number | string;
  icon: React.ReactNode;
  color: string;
  bgGradient: string;
}

interface QuickAction {
  title: string;
  icon: React.ReactNode;
  path: string;
  color: string;
}

interface RecentActivity {
  id: string;
  type: 'login' | 'operation' | 'system';
  title: string;
  description: string;
  time: string;
  user?: string;
}

function Home() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [statistics, setStatistics] = useState<StatisticCard[]>([]);
  const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);
  const [recentActivities, setRecentActivities] = useState<RecentActivity[]>([]);

  const quickActions: QuickAction[] = [
    { title: '用户管理', icon: <UserOutlined />, path: '/admin/user', color: '#6366f1' },
    { title: '角色管理', icon: <TeamOutlined />, path: '/admin/role', color: '#8b5cf6' },
    { title: '菜单管理', icon: <MenuOutlined />, path: '/admin/menu', color: '#ec4899' },
    { title: '部门管理', icon: <ApartmentOutlined />, path: '/admin/dept', color: '#10b981' },
    { title: '岗位管理', icon: <FileTextOutlined />, path: '/admin/post', color: '#f59e0b' },
    { title: '参数配置', icon: <SettingOutlined />, path: '/admin/config', color: '#3b82f6' },
    { title: '字典管理', icon: <DatabaseOutlined />, path: '/admin/dict', color: '#ef4444' },
    { title: '定时任务', icon: <ClockCircleOutlined />, path: '/admin/job', color: '#f97316' },
    { title: '服务器监控', icon: <BarChartOutlined />, path: '/admin/server', color: '#06b6d4' },
    { title: '系统日志', icon: <FileTextOutlined />, path: '/admin/syslog', color: '#64748b' },
    { title: '审计日志', icon: <SafetyOutlined />, path: '/admin/audit', color: '#84cc16' },
    { title: '代码生成器', icon: <CodeOutlined />, path: '/admin/generator', color: '#d946ef' },
  ];

  const handleQuickActionClick = useCallback((path: string) => {
    return (e: React.MouseEvent<HTMLButtonElement>) => {
      e.preventDefault();
      e.stopPropagation();
      navigate(path);
    };
  }, [navigate]);

  const convertApiActivityToActivity = (apiActivity: ApiRecentActivity): RecentActivity => {
    let time = '未知时间';
    if (apiActivity.time) {
      const date = dayjs(apiActivity.time);
      if (date.isValid()) {
        time = date.fromNow();
      }
    }

    return {
      id: apiActivity.id,
      type: apiActivity.type,
      title: apiActivity.title,
      description: apiActivity.description,
      time,
      user: apiActivity.user,
    };
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const [statisticsRes, systemInfoRes, activitiesRes] = await Promise.allSettled([
          getStatisticsApi(),
          getSystemInfoApi(),
          getRecentActivitiesApi(10),
        ]);

        const stats: StatisticCard[] = [];

        if (statisticsRes.status === 'fulfilled' && statisticsRes.value.data) {
          const data = statisticsRes.value.data;
          stats.push(
            {
              title: '用户总数',
              value: data.userCount || 0,
              icon: <UserOutlined />,
              color: '#6366f1',
              bgGradient: 'linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(99, 102, 241, 0.05) 100%)'
            },
            {
              title: '角色数量',
              value: data.roleCount || 0,
              icon: <TeamOutlined />,
              color: '#8b5cf6',
              bgGradient: 'linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(139, 92, 246, 0.05) 100%)'
            },
            {
              title: '部门数量',
              value: data.deptCount || 0,
              icon: <ApartmentOutlined />,
              color: '#10b981',
              bgGradient: 'linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(16, 185, 129, 0.05) 100%)'
            },
            {
              title: '在线会话',
              value: data.sessionCount || 0,
              icon: <WifiOutlined />,
              color: '#f59e0b',
              bgGradient: 'linear-gradient(135deg, rgba(245, 158, 11, 0.1) 0%, rgba(245, 158, 11, 0.05) 100%)'
            }
          );
        }

        setStatistics(stats);

        if (systemInfoRes.status === 'fulfilled' && systemInfoRes.value.data) {
          setSystemInfo(systemInfoRes.value.data);
        }

        if (activitiesRes.status === 'fulfilled' && activitiesRes.value.data) {
          const activities = activitiesRes.value.data
            .map(convertApiActivityToActivity)
            .filter(activity => activity.id);
          setRecentActivities(activities);
        } else {
          setRecentActivities([]);
        }
      } catch (error) {
        console.error('加载数据失败:', error);
        setRecentActivities([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const getActivityIcon = (type: string) => {
    switch (type) {
      case 'login':
        return <SafetyOutlined style={{ color: '#10b981' }} />;
      case 'operation':
        return <ToolOutlined style={{ color: '#6366f1' }} />;
      case 'system':
        return <DatabaseOutlined style={{ color: '#f59e0b' }} />;
      default:
        return <FileTextOutlined />;
    }
  };


  return (
    <div className="home-container fade-in">
      <div className="home-header-section">
        <div className="welcome-banner modern-card">
          <div className="welcome-content">
            <Title level={2} style={{ margin: '0 0 8px 0', color: '#1e293b' }}>
              欢迎回来, SuperAdmin
            </Title>
            <Text type="secondary" style={{ fontSize: '16px' }}>
              {systemInfo?.platformName || 'Admin Pro 企业级管理系统'} - 准备好开始一天的工作了吗？
            </Text>
          </div>
          <div className="welcome-decoration">
            <ThunderboltOutlined style={{ fontSize: '120px', color: 'rgba(99, 102, 241, 0.05)' }} />
          </div>
        </div>
      </div>

      <Spin spinning={loading}>
        <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
          {statistics.map((stat, index) => (
            <Col xs={24} sm={12} lg={6} key={index}>
              <Card
                className="modern-card statistic-card"
                bordered={false}
                bodyStyle={{ padding: '24px' }}
              >
                <Statistic
                  title={<span style={{ color: '#64748b', fontSize: '14px' }}>{stat.title}</span>}
                  value={stat.value}
                  prefix={
                    <div className="statistic-icon-wrapper" style={{ color: stat.color, background: stat.bgGradient }}>
                      {stat.icon}
                    </div>
                  }
                  valueStyle={{ color: '#1e293b', fontWeight: 600, marginTop: '8px' }}
                />
              </Card>
            </Col>
          ))}
        </Row>

        <Row gutter={[24, 24]}>
          <Col xs={24} lg={16}>
            <Card
              title={
                <Space>
                  <ThunderboltOutlined style={{ color: '#6366f1' }} />
                  <span>快速操作</span>
                </Space>
              }
              className="modern-card quick-actions-card"
              bordered={false}
              style={{ height: '100%' }}
            >
              <Row gutter={[16, 16]}>
                {quickActions.map((action, index) => (
                  <Col xs={12} sm={8} md={6} lg={4} key={index}>
                    <Button
                      type="text"
                      block
                      className="quick-action-btn"
                      onClick={handleQuickActionClick(action.path)}
                    >
                      <div className="quick-action-icon" style={{ color: action.color, background: `${action.color}15` }}>
                        {action.icon}
                      </div>
                      <span className="quick-action-title">
                        {action.title}
                      </span>
                    </Button>
                  </Col>
                ))}
              </Row>
            </Card>
          </Col>

          <Col xs={24} lg={8}>
            <Card
              title={
                <Space>
                  <ClockCircleOutlined style={{ color: '#6366f1' }} />
                  <span>最近活动</span>
                </Space>
              }
              className="modern-card recent-activities-card"
              bordered={false}
              style={{ height: '100%' }}
            >
              {recentActivities.length > 0 ? (
                <List
                  dataSource={recentActivities}
                  className="activity-list custom-scrollbar"
                  style={{ maxHeight: '600px', overflowY: 'auto', paddingRight: '4px' }}
                  renderItem={(item) => {
                    const displayDescription = item.description.length > 50
                      ? item.description.substring(0, 50) + '...'
                      : item.description;
                    const needTooltip = item.description.length > 50;

                    return (
                      <List.Item className="activity-item">
                        <List.Item.Meta
                          avatar={
                            <Avatar
                              icon={getActivityIcon(item.type)}
                              style={{ backgroundColor: 'rgba(241, 245, 249, 0.8)', color: '#64748b' }}
                            />
                          }
                          title={
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                              <Text strong style={{ fontSize: '14px' }}>{item.title}</Text>
                              <Text type="secondary" style={{ fontSize: '12px' }}>{item.time}</Text>
                            </div>
                          }
                          description={
                            <div style={{ marginTop: '4px' }}>
                              {item.user && (
                                <Tag color="geekblue" style={{ marginRight: '8px', border: 'none', background: 'rgba(99, 102, 241, 0.1)', color: '#6366f1' }}>
                                  {item.user}
                                </Tag>
                              )}
                              {needTooltip ? (
                                <Tooltip title={item.description} placement="topLeft">
                                  <Text type="secondary" style={{ fontSize: 13, cursor: 'help' }}>
                                    {displayDescription}
                                  </Text>
                                </Tooltip>
                              ) : (
                                <Text type="secondary" style={{ fontSize: 13 }}>
                                  {displayDescription}
                                </Text>
                              )}
                            </div>
                          }
                        />
                      </List.Item>
                    );
                  }}
                />
              ) : (
                <Empty description="暂无活动记录" image={Empty.PRESENTED_IMAGE_SIMPLE} />
              )}
            </Card>
          </Col>
        </Row>

        <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
          <Col xs={24}>
            <Card
              title={
                <Space>
                  <DatabaseOutlined style={{ color: '#6366f1' }} />
                  <span>系统信息</span>
                </Space>
              }
              className="modern-card system-info-card"
              bordered={false}
            >
              <Descriptions
                column={{ xs: 1, sm: 2, md: 3 }}
                bordered
                size="middle"
                className="custom-descriptions"
              >
                <Descriptions.Item label="平台名称">
                  <Text strong>{systemInfo?.platformName || '-'}</Text>
                </Descriptions.Item>
                <Descriptions.Item label="平台简称">
                  <Text strong>{systemInfo?.platformShortName || '-'}</Text>
                </Descriptions.Item>
                {systemInfo?.releaseVersion && (
                  <Descriptions.Item label="版本号">
                    <Tag color="purple">{systemInfo.releaseVersion}</Tag>
                  </Descriptions.Item>
                )}
                {systemInfo?.buildVersion && (
                  <Descriptions.Item label="构建版本">
                    <Tag color="cyan">{systemInfo.buildVersion}</Tag>
                  </Descriptions.Item>
                )}
                <Descriptions.Item label="版权信息" span={systemInfo?.releaseVersion || systemInfo?.buildVersion ? 1 : 3}>
                  <Text type="secondary">{systemInfo?.copyRight || '-'}</Text>
                </Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>
        </Row>
      </Spin>
    </div>
  );
}

export default Home;
