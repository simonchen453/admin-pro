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
    Input
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
            title: '登录名',
            dataIndex: 'loginName',
            key: 'loginName',
            width: 120,
            render: (text) => <Space><UserOutlined />{text}</Space>
        },
        {
            title: '真实姓名',
            dataIndex: 'realName',
            key: 'realName',
            width: 100
        },
        {
            title: '用户域',
            dataIndex: 'userDomain',
            key: 'userDomain',
            width: 100,
            render: (text) => <Tag color="blue">{text}</Tag>
        },
        {
            title: '设备名称',
            dataIndex: 'deviceName',
            key: 'deviceName',
            render: (text: string, record: UserDeviceVo) => (
                <Space>
                    {getPlatformIcon(record.platform)}
                    {text}
                </Space>
            )
        },
        {
            title: '操作系统',
            dataIndex: 'platform',
            key: 'platform',
            width: 100
        },
        {
            title: 'IP地址',
            dataIndex: 'lastIp',
            key: 'lastIp',
            width: 130,
            render: (text) => <Space><GlobalOutlined />{text}</Space>
        },
        {
            title: '最后活跃时间',
            dataIndex: 'lastActiveAt',
            key: 'lastActiveAt',
            width: 170
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
                        onClick={() => handleKickout(record)}
                        disabled={record.isActive === 0}
                    >
                        强制下线
                    </Button>
                </Space>
            )
        }
    ];

    return (
        <div className="fade-in">
            <Card className="modern-card" title="系统设备管理">
                <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
                    <Form.Item name="loginName" label="登录名">
                        <Input placeholder="输入登录名" allowClear />
                    </Form.Item>
                    <Form.Item name="userDomain" label="用户域">
                        <Input placeholder="输入用户域" allowClear />
                    </Form.Item>
                    <Form.Item name="deviceName" label="设备名称">
                        <Input placeholder="输入设备名称" allowClear />
                    </Form.Item>
                    <Form.Item>
                        <Space>
                            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                                搜索
                            </Button>
                            <Button onClick={handleReset} icon={<ReloadOutlined />}>
                                重置
                            </Button>
                        </Space>
                    </Form.Item>
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
                            onChange: (page, pageSize) => setPagination({ current: page, pageSize }),
                        }}
                        size="middle"
                    />
                </div>
            </Card>
        </div>
    );
};

export default DeviceList;
