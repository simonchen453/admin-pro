import React, { useState, useEffect, useCallback } from 'react';
import { Button, Empty, Spin } from 'antd';
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
  BarChartOutlined
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

/* 首页重做的依据：管理员打开后台不是来被欢迎的，是来确认「系统现在是否正常、
   有没有需要我处理的事」。所以砍掉了渐变欢迎横幅和图标的彩色圆底。

   更要紧的一处：旧版四张统计卡上挂着 trend: 5.2 / 2.1 / 0 / -1.5，
   这四个数字是写死在代码里的装饰，接口从来没返回过同比。在一套管理真实
   权限的系统里显示编出来的数字是不能接受的 —— 一起删了。
   现在这条带子上的每个数都来自 /common/statistics。 */

interface StatisticItem {
  label: string;
  value: number | string;
  /** 有对应页面才给链接，没有就不给 —— 不编入口 */
  to?: string;
  toLabel?: string;
}

interface QuickAction {
  title: string;
  icon: React.ReactNode;
  path: string;
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
  { title: '用户管理', icon: <UserOutlined />, path: '/admin/user' },
  { title: '角色管理', icon: <TeamOutlined />, path: '/admin/role' },
  { title: '菜单管理', icon: <MenuOutlined />, path: '/admin/menu' },
  { title: '部门管理', icon: <ApartmentOutlined />, path: '/admin/dept' },
  { title: '岗位管理', icon: <FileTextOutlined />, path: '/admin/post' },
  { title: '参数配置', icon: <SettingOutlined />, path: '/admin/config' },
  { title: '字典管理', icon: <DatabaseOutlined />, path: '/admin/dict' },
  { title: '定时任务', icon: <ClockCircleOutlined />, path: '/admin/job' },
  { title: '服务器监控', icon: <BarChartOutlined />, path: '/admin/server' },
  { title: '系统日志', icon: <FileTextOutlined />, path: '/admin/syslog' },
  { title: '审计日志', icon: <SafetyOutlined />, path: '/admin/audit' },
];

function Home() {
  const navigate = useNavigate();
  const { currentUser } = useAuthStore();
  const [loading, setLoading] = useState(true);
  const [statistics, setStatistics] = useState<StatisticItem[]>([]);
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

  const fetchData = useCallback(async () => {
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
        setStatistics([
          { label: '用户总数', value: stats.userCount, to: '/admin/user', toLabel: '用户管理' },
          { label: '角色数量', value: stats.roleCount, to: '/admin/role', toLabel: '角色管理' },
          { label: '部门数量', value: stats.deptCount, to: '/admin/dept', toLabel: '部门管理' },
          { label: '在线会话', value: stats.sessionCount },
        ]);
      }

      if (activitiesRes.success && Array.isArray(activitiesRes.data)) {
        setRecentActivities(activitiesRes.data.map(convertApiActivityToActivity));
      }

      setFetchedAt(dayjs().format('YYYY-MM-DD HH:mm:ss'));
    } catch (error) {
      console.error('Failed to fetch home data:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  /* 动态里只有「登录成功」是我们确定知道结果的一类，给它一个绿点。
     其余的既不是成功也不是失败，就别用颜色暗示。 */
  const activityDotClass = (type: string) => (type === 'login' ? 'ok' : '');

  const displayName = currentUser?.realName || currentUser?.loginName || '管理员';

  if (loading) {
    return <div className="hm-loading"><Spin size="large" /></div>;
  }

  return (
    <>
      <div className="ap-pagehead">
        <div>
          <h1 className="ap-page-title">工作台</h1>
          <p className="ap-page-sub">
            欢迎回来，{displayName}
            {fetchedAt && <> · 数据截至 <span className="ap-mono">{fetchedAt}</span></>}
          </p>
        </div>
        <div className="ap-page-act">
          <Button onClick={fetchData}>刷新</Button>
          <Button type="primary" onClick={() => navigate('/admin/user')}>用户管理</Button>
        </div>
      </div>

      {/* 统计带：四个数字并排，靠竖直发丝线分开，不是四张浮着的卡片 */}
      {statistics.length > 0 && (
        <section className="ap-stats">
          {statistics.map((stat) => (
            <div className="ap-stat" key={stat.label}>
              <div className="ap-stat-l">{stat.label}</div>
              <div className="ap-stat-v">{stat.value}</div>
              {stat.to && (
                <div className="ap-stat-d">
                  <a className="ap-link" onClick={() => navigate(stat.to!)}>{stat.toLabel}</a>
                </div>
              )}
            </div>
          ))}
        </section>
      )}

      <div className="ap-cols ap-cols-2-1">
        <section className="ap-card">
          <div className="ap-card-h">
            <h2 className="ap-card-t">快速操作</h2>
            <span className="ap-card-n">{QUICK_ACTIONS.length} 个入口</span>
          </div>
          {/* 发丝线织成的格子，不是十一个彩色圆底图标 */}
          <div className="hm-acts">
            {QUICK_ACTIONS.map((action) => (
              <button
                type="button"
                className="hm-act"
                key={action.path + action.title}
                onClick={() => navigate(action.path)}
              >
                {action.icon}
                <span>{action.title}</span>
              </button>
            ))}
          </div>
        </section>

        <section className="ap-card">
          <div className="ap-card-h">
            <h2 className="ap-card-t">近期动态</h2>
            <div className="ap-card-a">
              <a className="ap-link" onClick={() => navigate('/admin/syslog')}>全部日志</a>
            </div>
          </div>
          {recentActivities.length > 0 ? (
            <ul className="ap-feed">
              {recentActivities.map((item) => (
                <li key={item.id}>
                  <span className={`ap-f-av ${activityDotClass(item.type)}`}>
                    {(item.user || item.title || '·').slice(0, 1)}
                  </span>
                  <span className="ap-f-t">
                    <span className="ap-f-time">{item.time}</span>
                    {item.user && <b>{item.user}</b>} {item.title}
                    {item.description && <span className="ap-f-m">{item.description}</span>}
                  </span>
                </li>
              ))}
            </ul>
          ) : (
            <div className="ap-card-b">
              <Empty description="暂无活动" image={Empty.PRESENTED_IMAGE_SIMPLE} />
            </div>
          )}
        </section>
      </div>

      <section className="ap-card">
        <div className="ap-card-h"><h2 className="ap-card-t">运行信息</h2></div>
        <div className="ap-card-b">
          <div className="ap-kv">
            <span className="ap-kv-k">系统名称</span>
            <span className="ap-kv-v">{systemInfo?.sys?.computerName || '-'}</span>
            <span className="ap-kv-k">操作系统</span>
            <span className="ap-kv-v">{systemInfo?.sys?.osName || '-'}</span>
            <span className="ap-kv-k">系统架构</span>
            <span className="ap-kv-v">{systemInfo?.sys?.osArch || '-'}</span>
            <span className="ap-kv-k">Java 版本</span>
            <span className="ap-kv-v">{systemInfo?.jvm?.version || '-'}</span>
            <span className="ap-kv-k">应用版本</span>
            <span className="ap-kv-v">{systemInfo?.releaseVersion || '-'}</span>
            <span className="ap-kv-k">构建版本</span>
            <span className="ap-kv-v">{systemInfo?.buildVersion || '-'}</span>
          </div>
        </div>
      </section>
    </>
  );
}

export default Home;
