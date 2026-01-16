import React from 'react';
import { Form, Input, Select, Button, Row, Col, TreeSelect } from 'antd';
import { SearchOutlined, ClearOutlined } from '@ant-design/icons';
import type { UserSearchForm as IUserSearchForm } from '../../../types';

const { Option } = Select;

/**
 * 用户搜索表单组件属性
 */
export interface UserSearchFormProps {
    /** 搜索回调 */
    onSearch: (values: IUserSearchForm) => void;
    /** 重置回调 */
    onReset: () => void;
    /** 是否正在加载 */
    loading?: boolean;
    /** 部门树数据 */
    deptTreeData?: Array<{ key: string; title: string; value: string; children?: any[] }>;
    /** 域列表 */
    domainList?: Array<{ id: string; name: string; display: string }>;
}

/**
 * 用户搜索表单组件
 * 
 * 从 UserList 组件中提取出来的独立搜索表单
 */
const UserSearchForm: React.FC<UserSearchFormProps> = ({
    onSearch,
    onReset,
    loading = false,
    deptTreeData = [],
    domainList = []
}) => {
    const [form] = Form.useForm<IUserSearchForm>();

    const handleSearch = () => {
        const values = form.getFieldsValue();
        onSearch(values);
    };

    const handleReset = () => {
        form.resetFields();
        onReset();
    };

    return (
        <Form
            form={form}
            layout="inline"
            style={{ marginBottom: 16 }}
        >
            <Row gutter={[16, 16]} style={{ width: '100%' }}>
                <Col xs={24} sm={12} md={6} lg={4}>
                    <Form.Item name="loginName" style={{ marginBottom: 0, width: '100%' }}>
                        <Input placeholder="登录名" allowClear />
                    </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={6} lg={4}>
                    <Form.Item name="realName" style={{ marginBottom: 0, width: '100%' }}>
                        <Input placeholder="真实姓名" allowClear />
                    </Form.Item>
                </Col>
                <Col xs={24} sm={12} md={6} lg={4}>
                    <Form.Item name="status" style={{ marginBottom: 0, width: '100%' }}>
                        <Select placeholder="状态" allowClear style={{ width: '100%' }}>
                            <Option value="active">启用</Option>
                            <Option value="inactive">停用</Option>
                        </Select>
                    </Form.Item>
                </Col>
                {deptTreeData.length > 0 && (
                    <Col xs={24} sm={12} md={6} lg={4}>
                        <Form.Item name="deptId" style={{ marginBottom: 0, width: '100%' }}>
                            <TreeSelect
                                placeholder="所属部门"
                                allowClear
                                treeData={deptTreeData}
                                style={{ width: '100%' }}
                            />
                        </Form.Item>
                    </Col>
                )}
                {domainList.length > 1 && (
                    <Col xs={24} sm={12} md={6} lg={4}>
                        <Form.Item name="userDomain" style={{ marginBottom: 0, width: '100%' }}>
                            <Select placeholder="用户域" allowClear style={{ width: '100%' }}>
                                {domainList.map(domain => (
                                    <Option key={domain.id} value={domain.name}>{domain.display}</Option>
                                ))}
                            </Select>
                        </Form.Item>
                    </Col>
                )}
                <Col xs={24} sm={24} md={12} lg={8}>
                    <Form.Item style={{ marginBottom: 0 }}>
                        <Button
                            type="primary"
                            icon={<SearchOutlined />}
                            onClick={handleSearch}
                            loading={loading}
                            style={{ marginRight: 8 }}
                        >
                            搜索
                        </Button>
                        <Button
                            icon={<ClearOutlined />}
                            onClick={handleReset}
                        >
                            重置
                        </Button>
                    </Form.Item>
                </Col>
            </Row>
        </Form>
    );
};

export default UserSearchForm;
