import React, { useState, useEffect } from 'react';
import {
    Table,
    Button,
    Space,
    Card,
    Tag,
    message,
    Modal,
    Form,
    Input,
    Row,
    Col
} from 'antd';
import {
    ReloadOutlined,
    StopOutlined,
    SearchOutlined,
    DesktopOutlined,
    MobileOutlined,
    AppleOutlined,
    AndroidOutlined,
    GlobalOutlined,
    UserOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getAllDevicesApi, kickoutAnyDeviceApi } from '../../api/device';

interface UserDeviceVo {
    id: string;
    userId: string;
    loginName: string;
    realName: string;
    userDomain: string;
    deviceId: string;
    deviceName: string;
    platform: string;
    lastIp: string;
    lastActiveAt: string;
    isCurrent: boolean;
    isActive: number;
}

const DeviceList: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [list, setList] = useState<UserDeviceVo[]>([]);
    const [total, setTotal] = useState(0);
    const [form] = Form.useForm();
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10
    });

    useEffect(() => {
        fetchData();
    }, [pagination.current, pagination.pageSize]);

    const fetchData = async () => {
        setLoading(true);
        try {
            const values = await form.validateFields();
            const params = {
                ...values,
                pageNo: pagination.current,
                pageSize: pagination.pageSize
            };

            const response = await getAllDevicesApi(params);
            if (response.restCode === '200') {
                setList(response.data.records || []);
                setTotal(response.data.totalCount || 0);
            } else {
                message.error(response.message || '加载设备列表失败');
            }
        } catch (error) {
            console.error('获取设备列表失败:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = () => {
        setPagination({ ...pagination, current: 1 });
        fetchData();
    };

    const handleReset = () => {
        form.resetFields();
        handleSearch();
    };

    const handleKickout = (record: UserDeviceVo) => {
        Modal.confirm({
            title: '确认强制下线',
            content: `确定要强制下线用户 ${record.loginName}(${record.realName}) 的设备 "${record.deviceName}" 吗？`,
            okText: '确认',
            cancelText: '取消',
            onOk: async () => {
                try {
                    const response = await kickoutAnyDeviceApi(record.userId, record.deviceId);
                    if (response.restCode === '200') {
                        message.success('设备已下线');
                        fetchData();
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
        const p = (platform || '').toLowerCase();
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
            title: '用户信息',
            key: 'userInfo',
            render: (_, record) => (
                <Space>
                    <div style={{ padding: 8, background: '#f0f5ff', borderRadius: '50%' }}>
                        <UserOutlined style={{ color: '#2f54eb' }} />
                    </div>
                    <div>
                        <div style={{ fontWeight: 500 }}>{record.realName}</div>
                        <div style={{ fontSize: 12, color: '#8c8c8c' }}>@{record.loginName}</div>
                    </div>
                </Space>
            )
        },
        {
            title: '用户域',
            dataIndex: 'userDomain',
            key: 'userDomain',
            render: (text) => <Tag>{text}</Tag>
        },
        {
            title: '设备信息',
            key: 'deviceInfo',
            render: (_, record) => (
                <Space>
                    <div style={{ fontSize: 18, color: '#595959' }}>
                        {getPlatformIcon(record.platform)}
                    </div>
                    <div>
                        <div style={{ fontWeight: 500 }}>{record.deviceName}</div>
                        <div style={{ fontSize: 12, color: '#bfbfbf' }}>{record.platform}</div>
                    </div>
                </Space>
            )
        },
        {
            title: 'IP地址',
            dataIndex: 'lastIp',
            key: 'lastIp',
            width: 160,
            render: (text) => <div style={{ fontFamily: 'monospace' }}>{text}</div>
        },
        {
            title: '状态',
            key: 'status',
            width: 100,
            render: (_, record) => (
                 <Tag color={record.isActive === 1 ? 'success' : 'default'}>
                    {record.isActive === 1 ? '在线' : '离线'}
                 </Tag>
            )
        },
        {
            title: '最后活跃时间',
            dataIndex: 'lastActiveAt',
            key: 'lastActiveAt',
            width: 250,
            render: (text) => <span style={{ color: '#8c8c8c' }}>{text}</span>
        },
        {
            title: '操作',
            key: 'action',
            width: 100,
            render: (_, record) => (
                <Button
                    type="link"
                    danger
                    size="small"
                    icon={<StopOutlined />}
                    onClick={() => handleKickout(record)}
                    disabled={record.isActive === 0}
                >
                    强制下线
                </Button>
            )
        }
    ];

    return (
        <div className="fade-in">
            <Card className="modern-card" title="系统设备管理" bordered={false}>
                <Form form={form} layout="vertical" className="search-form">
                    <Row gutter={24}>
                        <Col xs={24} sm={8} md={6}>
                            <Form.Item name="loginName" label="登录名">
                                <Input placeholder="请输入登录名" prefix={<UserOutlined style={{ color: '#d9d9d9' }} />} allowClear />
                            </Form.Item>
                        </Col>
                        <Col xs={24} sm={8} md={6}>
                            <Form.Item name="userDomain" label="用户域">
                                <Input placeholder="请输入用户域" prefix={<GlobalOutlined style={{ color: '#d9d9d9' }} />} allowClear />
                            </Form.Item>
                        </Col>
                        <Col xs={24} sm={8} md={6}>
                            <Form.Item name="deviceName" label="设备名称">
                                <Input placeholder="请输入设备名称" prefix={<DesktopOutlined style={{ color: '#d9d9d9' }} />} allowClear />
                            </Form.Item>
                        </Col>
                        <Col xs={24} sm={24} md={6} style={{ display: 'flex', alignItems: 'flex-end', paddingBottom: 24 }}>
                            <Space>
                                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                                    搜索
                                </Button>
                                <Button onClick={handleReset} icon={<ReloadOutlined />}>
                                    重置
                                </Button>
                            </Space>
                        </Col>
                    </Row>
                </Form>

                <div className="modern-table">
                    <Table
                        columns={columns}
                        dataSource={list}
                        loading={loading}
                        rowKey="id"
                        pagination={{
                            current: pagination.current,
                            pageSize: pagination.pageSize,
                            total: total,
                            showSizeChanger: true,
                            showQuickJumper: true,
                            showTotal: (total) => `共 ${total} 条记录`,
                            onChange: (page, pageSize) => setPagination({ current: page, pageSize }),
                        }}
                    />
                </div>
            </Card>
        </div>
    );
};

export default DeviceList;
