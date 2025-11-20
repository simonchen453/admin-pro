import React, { useState, useEffect } from 'react';
import {
  Card,
  Row,
  Col,
  Table,
  Breadcrumb,
  Button,
  message,
  Spin
} from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getServerInfoApi } from '../../api/server';
import type { ServerInfo, ServerSysFileInfo } from '../../types';

const ServerInfo: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [serverInfo, setServerInfo] = useState<ServerInfo>({});

  const fetchServerInfo = async () => {
    setLoading(true);
    try {
      const data = await getServerInfoApi();
      setServerInfo(data);
    } catch (error) {
      console.error('获取服务器信息失败:', error);
      message.error('获取服务器信息失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServerInfo();
  }, []);

  const cpuColumns = [
    {
      title: '属性',
      dataIndex: 'label',
      key: 'label',
      width: '50%',
    },
    {
      title: '值',
      dataIndex: 'value',
      key: 'value',
      width: '50%',
    },
  ];

  const memColumns = [
    {
      title: '属性',
      dataIndex: 'label',
      key: 'label',
      width: '33%',
    },
    {
      title: '内存',
      dataIndex: 'mem',
      key: 'mem',
      width: '33%',
    },
    {
      title: 'JVM',
      dataIndex: 'jvm',
      key: 'jvm',
      width: '34%',
    },
  ];

  const diskColumns = [
    {
      title: '盘符路径',
      dataIndex: 'dirName',
      key: 'dirName',
    },
    {
      title: '文件系统',
      dataIndex: 'sysTypeName',
      key: 'sysTypeName',
    },
    {
      title: '盘符类型',
      dataIndex: 'typeName',
      key: 'typeName',
    },
    {
      title: '总大小',
      dataIndex: 'total',
      key: 'total',
    },
    {
      title: '可用大小',
      dataIndex: 'free',
      key: 'free',
    },
    {
      title: '已用大小',
      dataIndex: 'used',
      key: 'used',
    },
    {
      title: '已用百分比',
      dataIndex: 'usage',
      key: 'usage',
      render: (usage: number) => (
        <span style={{ color: usage > 80 ? '#ff4d4f' : undefined }}>
          {usage}%
        </span>
      ),
    },
  ];

  const cpuData = serverInfo.cpu
    ? [
        { key: '1', label: '核心数', value: serverInfo.cpu.cpuNum },
        { key: '2', label: '用户使用率', value: `${serverInfo.cpu.used}%` },
        { key: '3', label: '系统使用率', value: `${serverInfo.cpu.sys}%` },
        { key: '4', label: '当前空闲率', value: `${serverInfo.cpu.free}%` },
      ]
    : [];

  const memData = serverInfo.mem && serverInfo.jvm
    ? [
        {
          key: '1',
          label: '总内存',
          mem: `${serverInfo.mem.total}G`,
          jvm: `${serverInfo.jvm.total}M`,
        },
        {
          key: '2',
          label: '已用内存',
          mem: `${serverInfo.mem.used}G`,
          jvm: `${serverInfo.jvm.used}M`,
        },
        {
          key: '3',
          label: '剩余内存',
          mem: `${serverInfo.mem.free}G`,
          jvm: `${serverInfo.jvm.free}M`,
        },
        {
          key: '4',
          label: '使用率',
          mem: (
            <span style={{ color: serverInfo.mem.usage > 80 ? '#ff4d4f' : undefined }}>
              {serverInfo.mem.usage}%
            </span>
          ),
          jvm: (
            <span style={{ color: serverInfo.jvm.usage > 80 ? '#ff4d4f' : undefined }}>
              {serverInfo.jvm.usage}%
            </span>
          ),
        },
      ]
    : [];

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
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
              ),
            },
            {
              title: '服务器信息',
            },
          ]}
        />
      </div>

      <Spin spinning={loading}>
        {!loading && (
          <Row gutter={[20, 20]}>
            <Col xs={24} sm={24} md={12}>
              <Card title="CPU" size="small">
                <Table
                  columns={cpuColumns}
                  dataSource={cpuData}
                  pagination={false}
                  size="small"
                  bordered
                />
              </Card>
            </Col>

            <Col xs={24} sm={24} md={12}>
              <Card title="内存" size="small">
                <Table
                  columns={memColumns}
                  dataSource={memData}
                  pagination={false}
                  size="small"
                  bordered
                />
              </Card>
            </Col>

            <Col xs={24}>
              <Card title="服务器信息" size="small">
                <Row gutter={[16, 16]}>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>服务器名称：</strong>
                      {serverInfo.sys?.computerName || '-'}
                    </div>
                  </Col>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>操作系统：</strong>
                      {serverInfo.sys?.osName || '-'}
                    </div>
                  </Col>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>服务器IP：</strong>
                      {serverInfo.sys?.computerIp || '-'}
                    </div>
                  </Col>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>系统架构：</strong>
                      {serverInfo.sys?.osArch || '-'}
                    </div>
                  </Col>
                </Row>
              </Card>
            </Col>

            <Col xs={24}>
              <Card title="Java虚拟机信息" size="small">
                <Row gutter={[16, 16]}>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>Java名称：</strong>
                      {serverInfo.jvm?.name || '-'}
                    </div>
                  </Col>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>Java版本：</strong>
                      {serverInfo.jvm?.version || '-'}
                    </div>
                  </Col>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>启动时间：</strong>
                      {serverInfo.jvm?.startTime || '-'}
                    </div>
                  </Col>
                  <Col xs={24} sm={12}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>运行时长：</strong>
                      {serverInfo.jvm?.runTime || '-'}
                    </div>
                  </Col>
                  <Col xs={24}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>安装路径：</strong>
                      {serverInfo.jvm?.home || '-'}
                    </div>
                  </Col>
                  <Col xs={24}>
                    <div style={{ marginBottom: 8 }}>
                      <strong>项目路径：</strong>
                      {serverInfo.sys?.userDir || '-'}
                    </div>
                  </Col>
                </Row>
              </Card>
            </Col>

            <Col xs={24}>
              <Card title="磁盘状态" size="small">
                <Table
                  columns={diskColumns}
                  dataSource={serverInfo.sysFiles || []}
                  pagination={false}
                  size="small"
                  bordered
                  rowKey={(record: ServerSysFileInfo, index: number) => 
                    record.dirName || `disk-${index}`
                  }
                />
              </Card>
            </Col>
          </Row>
        )}
      </Spin>
    </div>
  );
};

export default ServerInfo;

