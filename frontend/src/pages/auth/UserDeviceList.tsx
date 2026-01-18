import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Card,
  Tag,
  message,
  Modal,
  Tooltip
} from 'antd';
import {
  ReloadOutlined,
  StopOutlined,
  DesktopOutlined,
  MobileOutlined,
  AppleOutlined,
  AndroidOutlined,
  QuestionCircleOutlined,
  ChromeOutlined,
  GlobalOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getDeviceListApi, kickoutDeviceApi } from '../../api/device';

interface UserDeviceVo {
  deviceId: string;
  deviceName: string;
  platform: string;
  lastIp: string;
  lastActiveAt: string;
  isCurrent: boolean;
}

const UserDeviceList: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [deviceList, setDeviceList] = useState<UserDeviceVo[]>([]);

  useEffect(() => {
    fetchDeviceList();
  }, []);

  const fetchDeviceList = async () => {
    setLoading(true);
    try {
      const response = await getDeviceListApi();
      if (response.restCode === '200') {
        setDeviceList(response.data || []);
      } else {
        message.error(response.message || '加载设备列表失败');
      }
    } catch (error) {
      console.error('获取设备列表失败:', error);
      message.error('加载设备列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleKickout = (record: UserDeviceVo) => {
    Modal.confirm({
      title: '确认强制下线',
      content: `确定要强制下线设备 "${record.deviceName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await kickoutDeviceApi(record.deviceId);
          if (response.restCode === '200') {
            message.success('设备已下线');
            fetchDeviceList();
          } else {
            message.error(response.message || '下线设备失败');
          }
        } catch (error) {
          console.error('下线设备失败:', error);
          message.error('下线设备失败');
        }
      }
    });
  };

  const getPlatformIcon = (platform: string) => {
    const p = platform.toLowerCase();
    if (p.includes('windows') || p.includes('mac') || p.includes('linux')) {
        if (p.includes('mac')) return <AppleOutlined />;
        return <DesktopOutlined />;
    }
    if (p.includes('android')) return <AndroidOutlined />;
    if (p.includes('iphone') || p.includes('ipad') || p.includes('ios')) return <AppleOutlined />;
    return <MobileOutlined />;
  };

  const columns: ColumnsType<UserDeviceVo> = [
    {
      title: '序号',
      key: 'index',
      width: 60,
      render: (_: any, __: any, index: number) => index + 1
    },
    {
      title: '设备名称',
      dataIndex: 'deviceName',
      key: 'deviceName',
      render: (text: string, record: UserDeviceVo) => (
        <Space>
          {getPlatformIcon(record.platform)}
          {text}
          {record.isCurrent && <Tag color="blue">当前设备</Tag>}
        </Space>
      )
    },
    {
      title: '操作系统',
      dataIndex: 'platform',
      key: 'platform',
      width: 150
    },
    {
      title: 'IP地址',
      dataIndex: 'lastIp',
      key: 'lastIp',
      width: 150,
      render: (text) => <Space><GlobalOutlined />{text}</Space>
    },
    {
      title: '最后活跃时间',
      dataIndex: 'lastActiveAt',
      key: 'lastActiveAt',
      width: 180
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_, record) => (
        <Space size="small">
          <Button
            size="small"
            danger
            icon={<StopOutlined />}
            disabled={record.isCurrent}
            onClick={() => handleKickout(record)}
          >
            强制下线
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div className="fade-in">
      <Card 
        className="modern-card" 
        title="在线设备管理" 
        extra={
            <Button 
                type="primary" 
                ghost 
                icon={<ReloadOutlined />} 
                onClick={fetchDeviceList} 
                loading={loading}
            >
                刷新列表
            </Button>
        }
      >
        <div className="modern-table">
            <Table
            columns={columns}
            dataSource={deviceList}
            loading={loading}
            rowKey="deviceId"
            pagination={false}
            size="middle"
            locale={{
                emptyText: deviceList.length === 0 && !loading ? '暂无在线设备' : undefined
            }}
            />
        </div>
      </Card>
    </div>
  );
};

export default UserDeviceList;
