import React from 'react';
import { Card, Row, Col, Statistic, Table } from 'antd';
import { UserOutlined, ShoppingCartOutlined, DollarOutlined, EyeOutlined } from '@ant-design/icons';

interface StatisticItem {
    title: string;
    value: number;
    icon: React.ReactNode;
    color: string;
    suffix?: string;
}

interface OrderItem {
    key: string;
    orderId: string;
    customer: string;
    amount: number;
    status: string;
}

const Dashboard: React.FC = () => {
    // 模拟数据
    const statistics: StatisticItem[] = [
        { title: '总用户数', value: 1128, icon: <UserOutlined />, color: '#1890ff' },
        { title: '订单数量', value: 93, icon: <ShoppingCartOutlined />, color: '#52c41a' },
        { title: '总收入', value: 112893, icon: <DollarOutlined />, color: '#faad14', suffix: '元' },
        { title: '访问量', value: 1128, icon: <EyeOutlined />, color: '#f5222d' },
    ];

    const recentOrders: OrderItem[] = [
        { key: '1', orderId: 'ORD001', customer: '张三', amount: 299, status: '已完成' },
        { key: '2', orderId: 'ORD002', customer: '李四', amount: 599, status: '处理中' },
        { key: '3', orderId: 'ORD003', customer: '王五', amount: 199, status: '待支付' },
    ];

    const columns = [
        { title: '订单号', dataIndex: 'orderId', key: 'orderId' },
        { title: '客户', dataIndex: 'customer', key: 'customer' },
        { title: '金额', dataIndex: 'amount', key: 'amount', render: (value: number) => `¥${value}` },
        { title: '状态', dataIndex: 'status', key: 'status' },
    ];

    return (
        <div>
            <h1>仪表盘</h1>
            
            {/* 统计卡片 */}
            <Row gutter={16} style={{ marginBottom: 24 }}>
                {statistics.map((stat, index) => (
                    <Col span={6} key={index}>
                        <Card>
                            <Statistic
                                title={stat.title}
                                value={stat.value}
                                prefix={stat.icon}
                                valueStyle={{ color: stat.color }}
                                suffix={stat.suffix}
                            />
                        </Card>
                    </Col>
                ))}
            </Row>

            {/* 最近订单 */}
            <Card title="最近订单" style={{ marginBottom: 24 }}>
                <Table
                    dataSource={recentOrders}
                    columns={columns}
                    pagination={false}
                    size="small"
                />
            </Card>

            {/* 其他内容区域 */}
            <Row gutter={16}>
                <Col span={12}>
                    <Card title="快速操作">
                        <p>这里可以添加快速操作按钮</p>
                    </Card>
                </Col>
                <Col span={12}>
                    <Card title="系统通知">
                        <p>这里可以显示系统通知信息</p>
                    </Card>
                </Col>
            </Row>
        </div>
    );
};

export default Dashboard;
