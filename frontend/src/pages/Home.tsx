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
  BarChartOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
  CheckCircleOutlined,
  SyncOutlined,
  CloseCircleOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getSystemInfoApi, getStatisticsApi, getRecentActivitiesApi, type RecentActivity as ApiRecentActivity } from '../api/common';
import type { SystemInfo } from '../types';
import { useAuthStore } from '../stores/useUserStore';
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
  trend?: number; // Mock trend data for visual effect
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
  const { currentUser } = useAuthStore();
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
      user: apiActivity.user
    };
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [sysInfo, stats, activities] = await Promise.all([
          getSystemInfoApi(),
          getStatisticsApi(),
          getRecentActivitiesApi()
        ]);

        setSystemInfo(sysInfo);

        setStatistics([
          {
            title: '用户总数',
            value: stats.userCount,
            icon: <UserOutlined />,
            color: '#6366f1',
            bgGradient: 'linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(99, 102, 241, 0.2) 100%)',
            trend: 5.2
          },
          {
            title: '角色数量',
            value: stats.roleCount,
            icon: <TeamOutlined />,
            color: '#8b5cf6',
            bgGradient: 'linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(139, 92, 246, 0.2) 100%)',
            trend: 2.1
          },
          {
            title: '部门数量',
            value: stats.deptCount,
            icon: <ApartmentOutlined />,
            color: '#10b981',
            bgGradient: 'linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(16, 185, 129, 0.2) 100%)',
            trend: 0
          },
          {
            title: '在线会话',
            value: stats.sessionCount,
            icon: <WifiOutlined />,
            color: '#f59e0b',
            bgGradient: 'linear-gradient(135deg, rgba(245, 158, 11, 0.1) 0%, rgba(245, 158, 11, 0.2) 100%)',
            trend: -1.5
          },
        ]);

        const formattedActivities = activities.map(convertApiActivityToActivity);
        setRecentActivities(formattedActivities);
      } catch (error) {
        console.error('Failed to fetch home data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const getActivityIcon = (type: string) => {
    switch (type) {
      case 'login':
        return <CheckCircleOutlined style={{ color: '#10b981' }} />;
      case 'operation':
        return <ToolOutlined style={{ color: '#3b82f6' }} />;
      case 'system':
        return <SafetyOutlined style={{ color: '#ef4444' }} />;
      default:
        return <ClockCircleOutlined style={{ color: '#64748b' }} />;
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="home-container fade-in">
      {/* Welcome Banner */}
      <div className="home-header-section">
        <Card className="welcome-banner" bordered={false}>
          <div className="welcome-content">
            <Title level={2} style={{ color: '#fff', marginBottom: 8 }}>
              欢迎回来，{currentUser?.realName || currentUser?.loginName || 'Admin'}
            </Title>
            <Text style={{ color: 'rgba(255, 255, 255, 0.85)', fontSize: '16px' }}>
              AdminPro 开发平台 - 准备好开始一天的工作了吗？
            </Text>
          </div>
          <div className="welcome-decoration">
            <ThunderboltOutlined style={{ fontSize: '120px', color: 'rgba(255, 255, 255, 0.1)' }} />
          </div>
        </Card>
      </div>

      {/* Statistics Cards */}
      <Row gutter={[24, 24]} style={{ marginBottom: 32 }}>
        {statistics.map((stat, index) => (
          <Col xs={24} sm={12} lg={6} key={index}>
            <Card className="statistic-card modern-card" bordered={false}>
              <div className="statistic-content">
                <div className="statistic-icon-wrapper" style={{ background: stat.bgGradient, color: stat.color }}>
                  {stat.icon}
                </div>
                <div className="statistic-info">
                  <Text type="secondary" style={{ fontSize: '14px' }}>{stat.title}</Text>
                  <div className="statistic-value-row">
                    <Title level={3} style={{ margin: 0 }}>{stat.value}</Title>
                    {stat.trend !== undefined && stat.trend !== 0 && (
                      <div className={`statistic-trend ${stat.trend > 0 ? 'trend-up' : 'trend-down'}`}>
                        {stat.trend > 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
                        <span>{Math.abs(stat.trend)}%</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[24, 24]}>
        {/* Quick Actions */}
        <Col xs={24} lg={16}>
          <Card
            title={<Space><ThunderboltOutlined style={{ color: '#6366f1' }} /><span>快速操作</span></Space>}
            className="modern-card"
            bordered={false}
            style={{ height: '100%' }}
          >
            <Row gutter={[16, 16]}>
              {quickActions.map((action, index) => (
                <Col xs={12} sm={8} md={6} lg={4} key={index}>
                  <Button
                    className="quick-action-btn"
                    onClick={handleQuickActionClick(action.path)}
                  >
                    <div className="quick-action-icon" style={{ color: action.color, background: `${action.color}15` }}>
                      {action.icon}
                    </div>
                    <span className="quick-action-title">{action.title}</span>
                  </Button>
                </Col>
              ))}
            </Row>
          </Card>
        </Col>

        {/* Recent Activities */}
        <Col xs={24} lg={8}>
          <Card
            title={<Space><ClockCircleOutlined style={{ color: '#6366f1' }} /><span>最近活动</span></Space>}
            className="modern-card"
            bordered={false}
            style={{ height: '100%' }}
          >
            <div className="activity-timeline">
              {recentActivities.length > 0 ? (
                <List
                  itemLayout="horizontal"
                  dataSource={recentActivities}
                  renderItem={(item) => (
                    <List.Item className="activity-item">
                      <List.Item.Meta
                        avatar={
                          <div className="activity-avatar">
                            {getActivityIcon(item.type)}
                          </div>
                        }
                        title={
                          <Space size={4}>
                            <Text strong>{item.title}</Text>
                            <Text type="secondary" style={{ fontSize: '12px' }}>{item.time}</Text>
                          </Space>
                        }
                        description={
                          <div className="activity-description">
                            {item.user && <Tag color="blue" style={{ marginRight: 4 }}>{item.user}</Tag>}
                            <Text type="secondary" ellipsis={{ tooltip: item.description }} style={{ maxWidth: 200 }}>
                              {item.description}
                            </Text>
                          </div>
                        }
                      />
                    </List.Item>
                  )}
                />
              ) : (
                <Empty description="暂无活动" image={Empty.PRESENTED_IMAGE_SIMPLE} />
              )}
            </div>
          </Card>
        </Col>
      </Row>

      {/* System Info */}
      <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
        <Col span={24}>
          <Card
            title={<Space><SettingOutlined style={{ color: '#6366f1' }} /><span>系统信息</span></Space>}
            className="modern-card"
            bordered={false}
          >
            <Descriptions bordered column={{ xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1 }} size="small" className="custom-descriptions">
              <Descriptions.Item label="系统名称">{systemInfo?.sys?.computerName || '-'}</Descriptions.Item>
              <Descriptions.Item label="操作系统">{systemInfo?.sys?.osName || '-'}</Descriptions.Item>
              <Descriptions.Item label="系统架构">{systemInfo?.sys?.osArch || '-'}</Descriptions.Item>
              <Descriptions.Item label="Java版本">{systemInfo?.jvm?.version || '-'}</Descriptions.Item>
              <Descriptions.Item label="Java路径">{systemInfo?.jvm?.home || '-'}</Descriptions.Item>
              <Descriptions.Item label="工作目录">{systemInfo?.sys?.userDir || '-'}</Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>
    </div>
  );
}

export default Home;
