import React, { useState, useEffect, useCallback } from 'react';
import { Button, Card, Row, Col, Avatar, Tag, Badge, Empty, Spin, Descriptions, Space } from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  ApartmentOutlined,
  SettingOutlined,
  MenuOutlined,
  FileTextOutlined,
  ClockCircleOutlined,
  DatabaseOutlined,
  SafetyOutlined,
  BarChartOutlined,
  ReloadOutlined,
  ArrowRightOutlined,
  CheckCircleOutlined,
  LoginOutlined,
  ThunderboltOutlined
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

interface QuickAction {
  title: string;
  icon: React.ReactNode;
  path: string;
  bgColor: string;
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

const QUICK_ACTIONS: QuickAction[] = [
  { title: '用户管理', icon: <UserOutlined />, path: '/admin/user', bgColor: '#e6f4ff', color: '#1677ff' },
  { title: '角色管理', icon: <TeamOutlined />, path: '/admin/role', bgColor: '#f9f0ff', color: '#722ed1' },
  { title: '菜单管理', icon: <MenuOutlined />, path: '/admin/menu', bgColor: '#fff7e6', color: '#fa8c16' },
  { title: '部门管理', icon: <ApartmentOutlined />, path: '/admin/dept', bgColor: '#e6fffb', color: '#13c2c2' },
  { title: '岗位管理', icon: <FileTextOutlined />, path: '/admin/post', bgColor: '#f0f5ff', color: '#2f54eb' },
  { title: '参数配置', icon: <SettingOutlined />, path: '/admin/config', bgColor: '#feffe6', color: '#faad14' },
  { title: '字典管理', icon: <DatabaseOutlined />, path: '/admin/dict', bgColor: '#fff0f6', color: '#eb2f96' },
  { title: '定时任务', icon: <ClockCircleOutlined />, path: '/admin/job', bgColor: '#fff2e8', color: '#fa541c' },
  { title: '服务器监控', icon: <BarChartOutlined />, path: '/admin/server', bgColor: '#fcffe6', color: '#a0d911' },
  { title: '系统日志', icon: <FileTextOutlined />, path: '/admin/syslog', bgColor: '#e6f4ff', color: '#1890ff' },
  { title: '审计日志', icon: <SafetyOutlined />, path: '/admin/audit', bgColor: '#f6ffed', color: '#52c41a' },
];

function Home() {
  const navigate = useNavigate();
  const { currentUser } = useAuthStore();
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [statistics, setStatistics] = useState({
    userCount: 0,
    roleCount: 0,
    deptCount: 0,
    sessionCount: 0,
  });
  const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);
  const [recentActivities, setRecentActivities] = useState<RecentActivity[]>([]);
  const [fetchedAt, setFetchedAt] = useState<string>('');

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

  const fetchData = useCallback(async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true);
    try {
      const [sysInfoRes, statsRes, activitiesRes] = await Promise.all([
        getSystemInfoApi(),
        getStatisticsApi(),
        getRecentActivitiesApi()
      ]);

      if (sysInfoRes.success) {
        setSystemInfo(sysInfoRes.data);
      }

      if (statsRes.success) {
        const stats = statsRes.data;
        setStatistics({
          userCount: stats.userCount || 0,
          roleCount: stats.roleCount || 0,
          deptCount: stats.deptCount || 0,
          sessionCount: stats.sessionCount || 0,
        });
      }

      if (activitiesRes.success && Array.isArray(activitiesRes.data)) {
        setRecentActivities(activitiesRes.data.map(convertApiActivityToActivity));
      }

      setFetchedAt(dayjs().format('YYYY-MM-DD HH:mm:ss'));
    } catch (error) {
      console.error('Failed to fetch home data:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const displayName = currentUser?.realName || currentUser?.loginName || '管理员';

  const getTimeGreeting = () => {
    const hour = dayjs().hour();
    if (hour < 6) return '凌晨好';
    if (hour < 9) return '早上好';
    if (hour < 12) return '上午好';
    if (hour < 14) return '中午好';
    if (hour < 18) return '下午好';
    return '晚上好';
  };

  if (loading) {
    return <div className="hm-loading"><Spin size="large" tip="正在加载工作台数据..." /></div>;
  }

  return (
    <div className="fade-in">
      {/* 顶部欢迎卡片 */}
      <div className="hm-header-card">
        <div className="hm-header-left">
          <Avatar size={54} className="hm-header-avatar">
            {displayName.slice(0, 1)}
          </Avatar>
          <div>
            <div className="hm-header-title">
              {getTimeGreeting()}，{displayName}，欢迎使用 {systemInfo?.platformShortName || 'AdminPro'}
            </div>
            <div className="hm-header-sub">
              系统当前运行平稳 · 数据更新于 {fetchedAt || '-'}
            </div>
          </div>
        </div>
        <Space>
          <Button
            icon={<ReloadOutlined spin={refreshing} />}
            onClick={() => fetchData(true)}
            loading={refreshing}
          >
            刷新数据
          </Button>
          <Button
            type="primary"
            icon={<UserOutlined />}
            onClick={() => navigate('/admin/user')}
          >
            用户管理
          </Button>
        </Space>
      </div>

      {/* 4 张核心指标统计卡 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} lg={6}>
          <div className="hm-stat-card">
            <div className="hm-stat-top">
              <span className="hm-stat-title">用户总数</span>
              <div className="hm-stat-icon-wrap hm-stat-icon-blue">
                <UserOutlined />
              </div>
            </div>
            <div className="hm-stat-value">{statistics.userCount}</div>
            <div className="hm-stat-footer">
              <span style={{ color: 'var(--ap-text-3)' }}>已开通系统账号</span>
              <a className="ap-link" onClick={() => navigate('/admin/user')}>
                进入管理 <ArrowRightOutlined style={{ fontSize: 10 }} />
              </a>
            </div>
          </div>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <div className="hm-stat-card">
            <div className="hm-stat-top">
              <span className="hm-stat-title">角色数量</span>
              <div className="hm-stat-icon-wrap hm-stat-icon-purple">
                <TeamOutlined />
              </div>
            </div>
            <div className="hm-stat-value">{statistics.roleCount}</div>
            <div className="hm-stat-footer">
              <span style={{ color: 'var(--ap-text-3)' }}>RBAC 权限体系</span>
              <a className="ap-link" onClick={() => navigate('/admin/role')}>
                角色分配 <ArrowRightOutlined style={{ fontSize: 10 }} />
              </a>
            </div>
          </div>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <div className="hm-stat-card">
            <div className="hm-stat-top">
              <span className="hm-stat-title">组织架构</span>
              <div className="hm-stat-icon-wrap hm-stat-icon-cyan">
                <ApartmentOutlined />
              </div>
            </div>
            <div className="hm-stat-value">{statistics.deptCount}</div>
            <div className="hm-stat-footer">
              <span style={{ color: 'var(--ap-text-3)' }}>部门层级组织</span>
              <a className="ap-link" onClick={() => navigate('/admin/dept')}>
                架构查看 <ArrowRightOutlined style={{ fontSize: 10 }} />
              </a>
            </div>
          </div>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <div className="hm-stat-card">
            <div className="hm-stat-top">
              <span className="hm-stat-title">活跃会话</span>
              <div className="hm-stat-icon-wrap hm-stat-icon-green">
                <SafetyOutlined />
              </div>
            </div>
            <div className="hm-stat-value">{statistics.sessionCount}</div>
            <div className="hm-stat-footer">
              <Badge status="processing" color="#52c41a" text="会话健康在线" />
              <Tag color="success">正常</Tag>
            </div>
          </div>
        </Col>
      </Row>

      {/* 中部内容：常用操作 + 近期动态 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} lg={16}>
          <Card
            className="hm-fill-card"
            title="常用功能快捷入口"
            extra={<Tag color="blue">{QUICK_ACTIONS.length} 个功能模块</Tag>}
          >
            <div className="hm-quick-grid">
              {QUICK_ACTIONS.map((action) => (
                <div
                  key={action.path}
                  className="hm-quick-item"
                  onClick={() => navigate(action.path)}
                >
                  <div
                    className="hm-quick-icon"
                    style={{ backgroundColor: action.bgColor, color: action.color }}
                  >
                    {action.icon}
                  </div>
                  <span className="hm-quick-text">{action.title}</span>
                </div>
              ))}
            </div>
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card
            className="hm-fill-card"
            title="近期操作动态"
            extra={<a className="ap-link" onClick={() => navigate('/admin/syslog')}>全部日志</a>}
          >
            {recentActivities.length > 0 ? (
              <div className="hm-activity-list">
                {recentActivities.slice(0, 6).map((item) => (
                  <div key={item.id} className="hm-activity-item">
                    <Avatar
                      size={32}
                      style={{
                        backgroundColor: item.type === 'login' ? '#e6f4ff' : '#f5f5f5',
                        color: item.type === 'login' ? '#1677ff' : '#595959',
                        flex: 'none'
                      }}
                    >
                      {item.type === 'login' ? <LoginOutlined /> : <ThunderboltOutlined />}
                    </Avatar>
                    <div className="hm-activity-content">
                      <div className="hm-activity-header">
                        <span className="hm-activity-title">
                          {item.user && <b>{item.user} </b>}
                          {item.title}
                        </span>
                        <span className="hm-activity-time">{item.time}</span>
                      </div>
                      {item.description && (
                        <div className="hm-activity-desc">{item.description}</div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <Empty description="暂无动态记录" image={Empty.PRESENTED_IMAGE_SIMPLE} />
            )}
          </Card>
        </Col>
      </Row>

      {/* 底部运行信息 */}
      <Card
        title="系统运行环境与状态"
        extra={<Badge status="success" text="运行正常" />}
      >
        <Descriptions bordered size="small" column={{ xxl: 4, xl: 3, lg: 3, md: 2, sm: 1, xs: 1 }}>
          <Descriptions.Item label="主机名称">
            {systemInfo?.sys?.computerName || 'localhost'}
          </Descriptions.Item>
          <Descriptions.Item label="操作系统">
            {systemInfo?.sys?.osName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="系统架构">
            {systemInfo?.sys?.osArch || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Java 运行环境">
            {systemInfo?.jvm?.version || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="系统版本">
            <Tag color="blue">{systemInfo?.releaseVersion || '1.0.0'}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="构建版本">
            {systemInfo?.buildVersion || 'Release'}
          </Descriptions.Item>
        </Descriptions>
      </Card>
    </div>
  );
}

export default Home;
