import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Button, List, Avatar, Tag, Space, Typography, Empty, Spin, Descriptions } from 'antd';
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
import { getUserListApi } from '../api/user';
import { getRoleListApi } from '../api/role';
import { getSessionListApi } from '../api/session';
import { getSystemInfoApi } from '../api/common';
import { getDeptListApi } from '../api/dept';
import type { SystemInfo } from '../types';
import './Home.css';

const { Title, Text } = Typography;

interface StatisticCard {
  title: string;
  value: number | string;
  icon: React.ReactNode;
  color: string;
  path?: string;
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

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const [userRes, roleRes, sessionRes, deptRes, systemInfoRes] = await Promise.allSettled([
          getUserListApi({ page: 1, pageSize: 1 }),
          getRoleListApi({ pageNo: 1, pageSize: 1 }),
          getSessionListApi({ pageNo: 1, pageSize: 1 }),
          getDeptListApi({}),
          getSystemInfoApi(),
        ]);

        const stats: StatisticCard[] = [];
        
        if (userRes.status === 'fulfilled') {
          stats.push({
            title: '用户总数',
            value: userRes.value.pagination?.total || 0,
            icon: <UserOutlined />,
            color: '#1890ff',
            path: '/admin/user',
          });
        }

        if (roleRes.status === 'fulfilled') {
          stats.push({
            title: '角色数量',
            value: roleRes.value.data?.totalCount || 0,
            icon: <TeamOutlined />,
            color: '#52c41a',
            path: '/admin/role',
          });
        }

        if (deptRes.status === 'fulfilled') {
          const deptData = deptRes.value.data;
          const deptCount = Array.isArray(deptData) ? deptData.length : 0;
          stats.push({
            title: '部门数量',
            value: deptCount,
            icon: <ApartmentOutlined />,
            color: '#faad14',
            path: '/admin/dept',
          });
        }

        if (sessionRes.status === 'fulfilled') {
          stats.push({
            title: '在线会话',
            value: sessionRes.value.data?.totalCount || 0,
            icon: <WifiOutlined />,
            color: '#722ed1',
            path: '/admin/session',
          });
        }

        setStatistics(stats);

        if (systemInfoRes.status === 'fulfilled' && systemInfoRes.value.data) {
          setSystemInfo(systemInfoRes.value.data);
        }

        const activities: RecentActivity[] = [
          {
            id: '1',
            type: 'login',
            title: '用户登录',
            description: '管理员登录系统',
            time: '刚刚',
            user: 'admin',
          },
          {
            id: '2',
            type: 'operation',
            title: '系统操作',
            description: '创建了新用户',
            time: '5分钟前',
            user: 'admin',
          },
          {
            id: '3',
            type: 'system',
            title: '系统通知',
            description: '定时任务执行完成',
            time: '10分钟前',
          },
        ];
        setRecentActivities(activities);
      } catch (error) {
        console.error('加载数据失败:', error);
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

  const handleStatisticClick = (path?: string) => {
    if (path) {
      navigate(path);
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
              <Card
                hoverable
                onClick={() => handleStatisticClick(stat.path)}
                className="statistic-card"
              >
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
            >
              <Row gutter={[12, 12]}>
                {quickActions.map((action, index) => (
                  <Col xs={12} sm={8} md={6} key={index}>
                    <Button
                      type="text"
                      block
                      className="quick-action-btn"
                      icon={action.icon}
                      onClick={() => navigate(action.path)}
                      style={{ 
                        height: '80px',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      <span style={{ marginTop: 8 }}>{action.title}</span>
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
            >
              {recentActivities.length > 0 ? (
                <List
                  dataSource={recentActivities}
                  renderItem={(item) => (
                    <List.Item className="activity-item">
                      <List.Item.Meta
                        avatar={<Avatar icon={getActivityIcon(item.type)} />}
                        title={
                          <Space>
                            <Text strong>{item.title}</Text>
                            {item.user && (
                              <Tag color="blue" size="small">{item.user}</Tag>
                            )}
                          </Space>
                        }
                        description={
                          <div>
                            <Text type="secondary" style={{ fontSize: 12 }}>
                              {item.description}
                            </Text>
                            <div style={{ marginTop: 4 }}>
                              <Text type="secondary" style={{ fontSize: 11 }}>
                                {item.time}
                              </Text>
                            </div>
                          </div>
                        }
                      />
                    </List.Item>
                  )}
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
