import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Row,
  Col,
  Table,
  Button,
  message,
  Spin,
  Progress,
  Tag,
  Descriptions,
  Typography,
  Space,
  Badge,
  Tooltip,
} from 'antd';
import {
  DashboardOutlined,
  FundOutlined,
  CoffeeOutlined,
  ClockCircleOutlined,
  DesktopOutlined,
  ThunderboltOutlined,
  HddOutlined,
  FolderOutlined,
  ReloadOutlined,
  LaptopOutlined,
  CloudServerOutlined,
  CheckCircleFilled,
  ExclamationCircleFilled,
} from '@ant-design/icons';
import { getServerInfoApi } from '../../api/server';
import type { ServerInfo, ServerSysFileInfo } from '../../types';
import './ServerInfo.css';

const { Text, Paragraph } = Typography;

const ServerInfoPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [serverInfo, setServerInfo] = useState<ServerInfo>({});
  const [lastUpdated, setLastUpdated] = useState<string>('');

  const fetchServerInfo = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getServerInfoApi();
      setServerInfo(data);
      const now = new Date();
      setLastUpdated(
        `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(
          now.getSeconds()
        ).padStart(2, '0')}`
      );
    } catch (error) {
      console.error('获取服务器信息失败:', error);
      message.error('获取服务器信息失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchServerInfo();
  }, [fetchServerInfo]);

  // CPU 使用率计算：用户使用率 + 系统使用率，或 100 - 空闲率
  const cpuUser = serverInfo.cpu?.used ?? 0;
  const cpuSys = serverInfo.cpu?.sys ?? 0;
  const cpuFree = serverInfo.cpu?.free ?? 100;
  const cpuUsage = Math.min(100, Math.max(0, Math.round((100 - cpuFree) * 100) / 100 || Math.round((cpuUser + cpuSys) * 100) / 100));

  // 内存使用率
  const memUsage = serverInfo.mem?.usage ? Math.round(serverInfo.mem.usage * 100) / 100 : 0;

  // JVM 使用率
  const jvmUsage = serverInfo.jvm?.usage ? Math.round(serverInfo.jvm.usage * 100) / 100 : 0;

  // 综合健康评级
  const isHealthy = cpuUsage < 85 && memUsage < 90;
  const isWarning = (cpuUsage >= 85 && cpuUsage < 95) || (memUsage >= 90 && memUsage < 95);

  // 仪表盘颜色映射
  const getProgressColor = (percent: number) => {
    if (percent >= 85) return '#ff4d4f';
    if (percent >= 70) return '#fa8c16';
    return '#1677ff';
  };

  // 磁盘表格列配置
  const diskColumns = [
    {
      title: '盘符路径',
      dataIndex: 'dirName',
      key: 'dirName',
      render: (dirName: string) => (
        <Space size={6} align="center">
          <FolderOutlined style={{ color: '#1677ff', fontSize: 14 }} />
          <Text code className="srv-path-code">{dirName}</Text>
        </Space>
      ),
    },
    {
      title: '文件系统',
      dataIndex: 'sysTypeName',
      key: 'sysTypeName',
      width: 120,
      render: (sysTypeName: string) => (
        <Tag color="blue">{sysTypeName || '未知'}</Tag>
      ),
    },
    {
      title: '盘符类型',
      dataIndex: 'typeName',
      key: 'typeName',
      render: (typeName: string) => typeName || '-',
    },
    {
      title: '总容量',
      dataIndex: 'total',
      key: 'total',
      align: 'right' as const,
      width: 110,
      render: (total: string) => <Text strong>{total}</Text>,
    },
    {
      title: '已用空间',
      dataIndex: 'used',
      key: 'used',
      align: 'right' as const,
      width: 110,
      render: (used: string) => <span style={{ color: '#8c8c8c' }}>{used}</span>,
    },
    {
      title: '可用空间',
      dataIndex: 'free',
      key: 'free',
      align: 'right' as const,
      width: 110,
      render: (free: string) => (
        <span style={{ color: '#52c41a', fontWeight: 600 }}>{free}</span>
      ),
    },
    {
      title: '空间使用率',
      dataIndex: 'usage',
      key: 'usage',
      width: 220,
      render: (usage: number) => {
        const num = Math.round((usage || 0) * 10) / 10;
        return (
          <Space direction="vertical" size={2} style={{ width: '100%' }}>
            <Progress
              percent={num}
              size="small"
              strokeColor={getProgressColor(num)}
              format={(p) => `${p}%`}
            />
          </Space>
        );
      },
    },
    {
      title: '运行状态',
      key: 'status',
      width: 110,
      align: 'center' as const,
      render: (_: unknown, record: ServerSysFileInfo) => {
        const u = record.usage || 0;
        if (u >= 90) {
          return <Badge status="error" text="严重不足" />;
        }
        if (u >= 80) {
          return <Badge status="warning" text="空间紧张" />;
        }
        return <Badge status="success" text="状态良好" />;
      },
    },
  ];

  return (
    <div className="srv-container fade-in">
      {/* 顶部系统运行状态横幅 */}
      <div className="srv-header-card">
        <div className="srv-header-left">
          <div className="srv-header-title-row">
            <span className="srv-header-title">服务运行监控</span>
            <span
              className={`srv-status-tag ${
                !isHealthy ? (isWarning ? 'warning' : 'danger') : ''
              }`}
            >
              <span className="srv-pulse-dot" />
              {isHealthy ? '系统运行正常' : isWarning ? '系统负载偏高' : '系统告警'}
            </span>
            <Tag icon={<LaptopOutlined />} color="blue">
              {serverInfo.sys?.computerIp || '127.0.0.1'} ({serverInfo.sys?.computerName || 'localhost'})
            </Tag>
            <Tag icon={<CloudServerOutlined />}>
              {serverInfo.sys?.osName} {serverInfo.sys?.osArch ? `(${serverInfo.sys.osArch})` : ''}
            </Tag>
          </div>
          <div className="srv-header-desc">
            <span>实时采集宿主机 CPU、物理内存、JVM 虚拟机及磁盘分区运行指标</span>
            {lastUpdated && <span>最后同步时间：{lastUpdated}</span>}
          </div>
        </div>

        <div className="srv-header-right">
          <Button
            type="primary"
            icon={<ReloadOutlined spin={loading} />}
            loading={loading}
            onClick={fetchServerInfo}
          >
            刷新数据
          </Button>
        </div>
      </div>

      <Spin spinning={loading} tip="正在采集服务器最新状态...">
        {/* 四张核心指标卡片 */}
        <Row gutter={[16, 16]}>
          {/* 1. CPU 使用率卡片 */}
          <Col xs={24} sm={12} lg={6}>
            <div className="srv-stat-card">
              <div className="srv-stat-card-head">
                <span className="srv-stat-card-title">
                  <DashboardOutlined style={{ color: '#1677ff' }} />
                  CPU 负载
                </span>
                <Tag color={cpuUsage > 80 ? 'red' : 'blue'}>
                  {serverInfo.cpu?.cpuNum ? `${serverInfo.cpu.cpuNum} 核心` : '多核'}
                </Tag>
              </div>

              <div className="srv-stat-card-body">
                <div className="srv-gauge-wrapper">
                  <Progress
                    type="dashboard"
                    percent={cpuUsage}
                    size={86}
                    strokeColor={getProgressColor(cpuUsage)}
                    format={(percent) => (
                      <div className="srv-gauge-text">
                        <span className="srv-gauge-val">{percent}%</span>
                        <span className="srv-gauge-sub">使用率</span>
                      </div>
                    )}
                  />
                </div>

                <div className="srv-metrics-list">
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">用户态占比</span>
                    <span className="srv-metric-value">{cpuUser}%</span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">系统态占比</span>
                    <span className="srv-metric-value">{cpuSys}%</span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">当前空闲率</span>
                    <span className="srv-metric-value" style={{ color: '#52c41a' }}>
                      {cpuFree}%
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </Col>

          {/* 2. 物理内存卡片 */}
          <Col xs={24} sm={12} lg={6}>
            <div className="srv-stat-card">
              <div className="srv-stat-card-head">
                <span className="srv-stat-card-title">
                  <FundOutlined style={{ color: '#52c41a' }} />
                  物理内存
                </span>
                <Tag color={memUsage > 80 ? 'red' : 'green'}>
                  {serverInfo.mem?.total ? `${serverInfo.mem.total} GB` : 'System'}
                </Tag>
              </div>

              <div className="srv-stat-card-body">
                <div className="srv-gauge-wrapper">
                  <Progress
                    type="dashboard"
                    percent={memUsage}
                    size={86}
                    strokeColor={getProgressColor(memUsage)}
                    format={(percent) => (
                      <div className="srv-gauge-text">
                        <span className="srv-gauge-val">{percent}%</span>
                        <span className="srv-gauge-sub">使用率</span>
                      </div>
                    )}
                  />
                </div>

                <div className="srv-metrics-list">
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">总物理内存</span>
                    <span className="srv-metric-value">{serverInfo.mem?.total ?? 0} GB</span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">已使用内存</span>
                    <span className="srv-metric-value">{serverInfo.mem?.used ?? 0} GB</span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">空闲内存</span>
                    <span className="srv-metric-value" style={{ color: '#52c41a' }}>
                      {serverInfo.mem?.free ?? 0} GB
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </Col>

          {/* 3. JVM 堆内存卡片 */}
          <Col xs={24} sm={12} lg={6}>
            <div className="srv-stat-card">
              <div className="srv-stat-card-head">
                <span className="srv-stat-card-title">
                  <CoffeeOutlined style={{ color: '#722ed1' }} />
                  JVM 堆内存
                </span>
                <Tag color="purple">
                  {serverInfo.jvm?.version ? `JDK ${serverInfo.jvm.version.split('.')[0]}` : 'Java'}
                </Tag>
              </div>

              <div className="srv-stat-card-body">
                <div className="srv-gauge-wrapper">
                  <Progress
                    type="dashboard"
                    percent={jvmUsage}
                    size={86}
                    strokeColor={getProgressColor(jvmUsage)}
                    format={(percent) => (
                      <div className="srv-gauge-text">
                        <span className="srv-gauge-val">{percent}%</span>
                        <span className="srv-gauge-sub">已用占比</span>
                      </div>
                    )}
                  />
                </div>

                <div className="srv-metrics-list">
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">已用堆内存</span>
                    <span className="srv-metric-value">{serverInfo.jvm?.used ?? 0} MB</span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">当前分配</span>
                    <span className="srv-metric-value">{serverInfo.jvm?.total ?? 0} MB</span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">最大可用</span>
                    <span className="srv-metric-value">{serverInfo.jvm?.max ?? 0} MB</span>
                  </div>
                </div>
              </div>
            </div>
          </Col>

          {/* 4. 运行环境与时长卡片 */}
          <Col xs={24} sm={12} lg={6}>
            <div className="srv-stat-card">
              <div className="srv-stat-card-head">
                <span className="srv-stat-card-title">
                  <ClockCircleOutlined style={{ color: '#13c2c2' }} />
                  服务运行时长
                </span>
                <Badge status="processing" text="活跃中" />
              </div>

              <div className="srv-uptime-box">
                <div className="srv-uptime-main">
                  <div className="srv-uptime-title">已持续平稳运行</div>
                  <div className="srv-uptime-val">
                    {serverInfo.jvm?.runTime || '0天0小时0分钟'}
                  </div>
                </div>

                <div className="srv-metrics-list">
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">启动时间</span>
                    <span className="srv-metric-value" style={{ fontSize: 12 }}>
                      {serverInfo.jvm?.startTime || '-'}
                    </span>
                  </div>
                  <div className="srv-metric-row">
                    <span className="srv-metric-label">挂载磁盘</span>
                    <span className="srv-metric-value">
                      {serverInfo.sysFiles?.length || 0} 个分区
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </Col>
        </Row>

        {/* 中间配置详情：服务器基础信息 & JVM虚拟机信息 */}
        <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
          <Col xs={24} lg={12}>
            <Card
              title={
                <Space>
                  <DesktopOutlined style={{ color: '#1677ff' }} />
                  <span>服务器基础信息</span>
                </Space>
              }
              bordered={false}
              className="srv-panel-card"
            >
              <Descriptions
                bordered
                size="small"
                column={1}
                className="srv-descriptions"
              >
                <Descriptions.Item label="服务器名称">
                  <Text strong>{serverInfo.sys?.computerName || '-'}</Text>
                </Descriptions.Item>
                <Descriptions.Item label="操作系统">
                  <Tag color="geekblue">{serverInfo.sys?.osName || '-'}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="服务器 IP">
                  <Text copyable={{ text: serverInfo.sys?.computerIp }}>
                    <Tag color="blue">{serverInfo.sys?.computerIp || '-'}</Tag>
                  </Text>
                </Descriptions.Item>
                <Descriptions.Item label="系统架构">
                  <Tag>{serverInfo.sys?.osArch || '-'}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="CPU 架构/核心">
                  <span>{serverInfo.cpu?.cpuNum ? `${serverInfo.cpu.cpuNum} 个逻辑核心` : '-'}</span>
                </Descriptions.Item>
                <Descriptions.Item label="项目部署路径">
                  <Paragraph
                    copyable
                    ellipsis={{ rows: 1, expandable: true, symbol: '展开' }}
                    style={{ margin: 0, fontFamily: 'monospace', fontSize: 12 }}
                  >
                    {serverInfo.sys?.userDir || '-'}
                  </Paragraph>
                </Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>

          <Col xs={24} lg={12}>
            <Card
              title={
                <Space>
                  <ThunderboltOutlined style={{ color: '#722ed1' }} />
                  <span>Java 虚拟机 (JVM) 配置</span>
                </Space>
              }
              bordered={false}
              className="srv-panel-card"
            >
              <Descriptions
                bordered
                size="small"
                column={1}
                className="srv-descriptions"
              >
                <Descriptions.Item label="Java 运行时">
                  <Text strong>{serverInfo.jvm?.name || '-'}</Text>
                </Descriptions.Item>
                <Descriptions.Item label="JDK 版本">
                  <Tag color="purple">{serverInfo.jvm?.version || '-'}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="服务启动时间">
                  <span>{serverInfo.jvm?.startTime || '-'}</span>
                </Descriptions.Item>
                <Descriptions.Item label="已运行时长">
                  <Tag color="green">{serverInfo.jvm?.runTime || '-'}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="堆内存限额">
                  <Space split={<span>/</span>}>
                    <span>当前: {serverInfo.jvm?.total || 0} MB</span>
                    <span>最大: {serverInfo.jvm?.max || 0} MB</span>
                  </Space>
                </Descriptions.Item>
                <Descriptions.Item label="Java 安装路径">
                  <Paragraph
                    copyable
                    ellipsis={{ rows: 1, expandable: true, symbol: '展开' }}
                    style={{ margin: 0, fontFamily: 'monospace', fontSize: 12 }}
                  >
                    {serverInfo.jvm?.home || '-'}
                  </Paragraph>
                </Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>
        </Row>

        {/* 底部磁盘分区状态表格 */}
        <Card
          title={
            <Space>
              <HddOutlined style={{ color: '#1677ff' }} />
              <span>磁盘分区挂载与存储状态</span>
            </Space>
          }
          bordered={false}
          className="srv-panel-card"
          style={{ marginTop: 16 }}
        >
          <Table
            columns={diskColumns}
            dataSource={serverInfo.sysFiles || []}
            pagination={false}
            size="middle"
            className="srv-disk-table"
            rowKey={(record: ServerSysFileInfo, index?: number) =>
              record.dirName || `disk-${index}`
            }
          />
        </Card>
      </Spin>
    </div>
  );
};

export default ServerInfoPage;
