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
  SafetyOutlined
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
    { title: '用户管理', icon: <UserOutlined />, path: '/admin/user', color: '#1890ff' },
    { title: '角色管理', icon: <TeamOutlined />, path: '/admin/role', color: '#52c41a' },
    { title: '菜单管理', icon: <MenuOutlined />, path: '/admin/menu', color: '#faad14' },
    { title: '部门管理', icon: <ApartmentOutlined />, path: '/admin/dept', color: '#722ed1' },
    { title: '岗位管理', icon: <FileTextOutlined />, path: '/admin/post', color: '#eb2f96' },
    { title: '参数配置', icon: <SettingOutlined />, path: '/admin/config', color: '#13c2c2' },
    { title: '字典管理', icon: <DatabaseOutlined />, path: '/admin/dict', color: '#f5222d' },
    { title: '定时任务', icon: <ClockCircleOutlined />, path: '/admin/job', color: '#fa8c16' },
    { title: '服务器监控', icon: <DatabaseOutlined />, path: '/admin/server', color: '#2f54eb' },
    { title: '系统日志', icon: <FileTextOutlined />, path: '/admin/syslog', color: '#fa541c' },
    { title: '审计日志', icon: <SafetyOutlined />, path: '/admin/audit', color: '#a0d911' },
    { title: '代码生成器', icon: <CodeOutlined />, path: '/admin/generator', color: '#eb2f96' },
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
              color: '#1890ff',
            },
            {
              title: '角色数量',
              value: data.roleCount || 0,
              icon: <TeamOutlined />,
              color: '#52c41a',
            },
            {
              title: '部门数量',
              value: data.deptCount || 0,
              icon: <ApartmentOutlined />,
              color: '#faad14',
            },
            {
              title: '在线会话',
              value: data.sessionCount || 0,
              icon: <WifiOutlined />,
              color: '#722ed1',
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
        return <SafetyOutlined style={{ color: '#52c41a' }} />;
      case 'operation':
        return <ToolOutlined style={{ color: '#1890ff' }} />;
      case 'system':
        return <DatabaseOutlined style={{ color: '#faad14' }} />;
      default:
        return <FileTextOutlined />;
    }
  };


  return (
    <div className="home-container">
      <div className="home-header">
        <Title level={2} style={{ margin: 0 }}>
          欢迎回来
        </Title>
        <Text type="secondary">
          {systemInfo?.platformName || 'Admin Pro 管理系统'}
        </Text>
      </div>

      <Spin spinning={loading}>
        <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
          {statistics.map((stat, index) => (
            <Col xs={24} sm={12} lg={6} key={index}>
              <Card className="statistic-card">
                <Statistic
                  title={stat.title}
                  value={stat.value}
                  prefix={stat.icon}
                  valueStyle={{ color: stat.color }}
                />
              </Card>
            </Col>
          ))}
        </Row>

        <Row gutter={[16, 16]}>
          <Col xs={24} lg={16}>
            <Card 
              title={
                <Space>
                  <ToolOutlined />
                  <span>快速操作</span>
                </Space>
              }
              className="quick-actions-card"
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
                      style={{ 
                        height: '90px',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        padding: '12px 8px',
                      }}
                    >
                      <div style={{ 
                        fontSize: '24px', 
                        marginBottom: '10px',
                        color: action.color,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}>
                        {action.icon}
                      </div>
                      <span style={{ 
                        fontSize: '13px',
                        fontWeight: 500,
                        lineHeight: '1.4',
                        color: '#333'
                      }}>
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
                  <ClockCircleOutlined />
                  <span>最近活动</span>
                </Space>
              }
              className="recent-activities-card"
              style={{ height: '100%' }}
            >
              {recentActivities.length > 0 ? (
                <List
                  dataSource={recentActivities}
                  style={{ maxHeight: '600px', overflowY: 'auto' }}
                  renderItem={(item) => {
                    const displayDescription = item.description.length > 50 
                      ? item.description.substring(0, 50) + '...' 
                      : item.description;
                    const needTooltip = item.description.length > 50;
                    
                    return (
                      <List.Item className="activity-item">
                        <List.Item.Meta
                          avatar={<Avatar icon={getActivityIcon(item.type)} />}
                          title={
                            <Space>
                              <Text strong>{item.title}</Text>
                              {item.user && (
                                <Tag color="blue">{item.user}</Tag>
                              )}
                            </Space>
                          }
                          description={
                            <div>
                              {needTooltip ? (
                                <Tooltip 
                                  title={
                                    <div style={{ 
                                      maxWidth: '500px', 
                                      wordBreak: 'break-word',
                                      whiteSpace: 'pre-wrap'
                                    }}>
                                      {item.description}
                                    </div>
                                  } 
                                  placement="topLeft"
                                  overlayStyle={{ maxWidth: '500px' }}
                                  overlayInnerStyle={{ 
                                    maxWidth: '500px',
                                    wordBreak: 'break-word',
                                    whiteSpace: 'pre-wrap'
                                  }}
                                >
                                  <Text type="secondary" style={{ fontSize: 12, cursor: 'help' }}>
                                    {displayDescription}
                                  </Text>
                                </Tooltip>
                              ) : (
                                <Text type="secondary" style={{ fontSize: 12 }}>
                                  {displayDescription}
                                </Text>
                              )}
                              <div style={{ marginTop: 4 }}>
                                <Text type="secondary" style={{ fontSize: 11 }}>
                                  {item.time}
                                </Text>
                              </div>
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

        <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
          <Col xs={24}>
            <Card 
              title={
                <Space>
                  <DatabaseOutlined />
                  <span>系统信息</span>
                </Space>
              }
              className="system-info-card"
            >
              <Descriptions 
                column={{ xs: 1, sm: 2, md: 3 }}
                bordered
                size="small"
              >
                <Descriptions.Item label="平台名称">
                  <Text strong>{systemInfo?.platformName || '-'}</Text>
                </Descriptions.Item>
                <Descriptions.Item label="平台简称">
                  <Text strong>{systemInfo?.platformShortName || '-'}</Text>
                </Descriptions.Item>
                {systemInfo?.releaseVersion && (
                  <Descriptions.Item label="版本号">
                    <Tag color="blue">{systemInfo.releaseVersion}</Tag>
                  </Descriptions.Item>
                )}
                {systemInfo?.buildVersion && (
                  <Descriptions.Item label="构建版本">
                    <Tag color="green">{systemInfo.buildVersion}</Tag>
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
