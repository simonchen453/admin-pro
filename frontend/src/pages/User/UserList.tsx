import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Select,
  Card,
  Tag,
  message,
  Modal,
  Row,
  Col,
  Pagination,
  Tree,
  Breadcrumb,
  Divider
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  UserDeleteOutlined,
  UserAddOutlined,
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
  DeleteOutlined,
  HomeOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  activeUserApi,
  inactiveUserApi,
  getUserPrepareDataApi,
  getUserListApi,
  getDeptTreeSelectApi,
  getDomainListApi,
  deleteUserApi,
  resetPasswordApi
} from '../../api/user';
import type {
  UserEntity,
  UserSearchForm,
  RoleEntity,
  PostEntity
} from '../../types';
import { UserStatus } from '../../types';
import UserForm from './UserForm';

const { Option } = Select;

const UserList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [resetForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [userList, setUserList] = useState<UserEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<UserSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedUsers, setSelectedUsers] = useState<UserEntity[]>([]);
  
  // 模态框状态
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<UserEntity | null>(null);
  const [formKey, setFormKey] = useState(0);
  const [resetModalVisible, setResetModalVisible] = useState(false);
  const [resetTargetUser, setResetTargetUser] = useState<UserEntity | null>(null);
  const [resetLoading, setResetLoading] = useState(false);
  
  // 下拉选项数据
  const [deptTreeData, setDeptTreeData] = useState<DeptTreeNode[]>([]);
  const [roleList, setRoleList] = useState<RoleEntity[]>([]);
  const [postList, setPostList] = useState<PostEntity[]>([]);
  const [domainList, setDomainList] = useState<Array<{ id: string; name: string; display: string }>>([]);

  // 部门树节点类型
  interface DeptTreeNode {
    key: string;
    title: string;
    children?: DeptTreeNode[];
  }

  // API返回的部门树数据格式
  interface DeptTreeApiResponse {
    id: string;
    label: string;
    children?: DeptTreeApiResponse[];
  }

  // 转换API返回的树形数据为Ant Design Tree需要的格式
  const convertDeptTree = (data: DeptTreeApiResponse[]): DeptTreeNode[] => {
    if (!data || data.length === 0) {
      return [];
    }

    return data.map(item => ({
      key: item.id,
      title: item.label,
      children: item.children ? convertDeptTree(item.children) : undefined
    }));
  };

  // 转换树形数据为TreeSelect需要的格式（添加value字段）
  const convertToTreeSelectData = (treeData: DeptTreeNode[]): Array<{
    key: string;
    title: string;
    value: string;
    children?: Array<{ key: string; title: string; value: string; children?: any[] }>;
  }> => {
    return treeData.map(node => ({
      key: node.key,
      title: node.title,
      value: node.key,
      children: node.children ? convertToTreeSelectData(node.children) : undefined
    }));
  };

  // 获取准备数据
  const fetchPrepareData = async () => {
    try {
      const [deptData, prepareData, domains] = await Promise.all([
        getDeptTreeSelectApi(),
        getUserPrepareDataApi(),
        getDomainListApi()
      ]);
      
      const apiDeptData = deptData as unknown as DeptTreeApiResponse[];
      const treeData = convertDeptTree(apiDeptData);
      
      setDeptTreeData(treeData);
      setRoleList(prepareData.roles || []);
      setPostList(prepareData.posts || []);
      setDomainList(domains || []);
    } catch (error) {
      console.error('获取准备数据失败:', error);
      setDeptTreeData([]);
      setRoleList([]);
      setPostList([]);
      setDomainList([]);
    }
  };

  // 获取用户列表
  const fetchUserList = async (params: UserSearchForm = {}) => {
    setLoading(true);
    
    try {
      const response = await getUserListApi({
        ...params,
        page: params.page ?? currentPage,
        pageSize: params.pageSize ?? pageSize
      });
      
      const responseData = response as any;
      const list = responseData?.list || responseData?.records || responseData?.data?.list || responseData?.data?.records || [];
      const total = responseData?.pagination?.total || responseData?.totalCount || responseData?.data?.pagination?.total || responseData?.data?.totalCount || 0;
      
      console.log('提取后的list:', list);
      console.log('提取后的total:', total);
      
      if (Array.isArray(list)) {
        setUserList(list);
        setTotal(total);
      } else {
        setUserList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取用户列表失败:', error);
      setUserList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  // 搜索
  const handleSearch = (values: UserSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchUserList(values);
  };

  // 重置搜索
  const handleReset = () => {
    form.resetFields();
    setSearchForm({});
    setCurrentPage(1);
    fetchUserList({});
  };

  // 分页变化
  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchUserList({ ...searchForm, page, pageSize: size || pageSize });
  };

  // 激活用户
  const handleActive = (user: UserEntity) => {
    Modal.confirm({
      title: '确认启用',
      content: `确定要启用用户 ${user.realName} 吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const result = await activeUserApi(user.userIden.userDomain, user.userIden.userId);
          if (result.success) {
            message.success('用户激活成功');
            fetchUserList(searchForm);
          } else {
            message.error(result.message || '用户激活失败');
          }
        } catch (error) {
          message.error('用户激活失败');
        }
      }
    });
  };

  // 停用用户
  const handleInactive = (user: UserEntity) => {
    Modal.confirm({
      title: '确认停用',
      content: `确定要停用用户 ${user.realName} 吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const result = await inactiveUserApi(user.userIden.userDomain, user.userIden.userId);
          if (result.success) {
            message.success('用户停用成功');
            fetchUserList(searchForm);
          } else {
            message.error(result.message || '用户停用失败');
          }
        } catch (error) {
          message.error('用户停用失败');
        }
      }
    });
  };

  // 重置密码
  const handleResetPassword = (user: UserEntity) => {
    setResetTargetUser(user);
    resetForm.resetFields();
    setResetModalVisible(true);
  };

  const handleResetPwdSubmit = async () => {
    if (!resetTargetUser) {
      return;
    }
    try {
      const values = await resetForm.validateFields();
      setResetLoading(true);
      await resetPasswordApi({
        userDomain: resetTargetUser.userIden.userDomain,
        userId: resetTargetUser.userIden.userId,
        newPassword: values.newPassword,
        confirmPassword: values.confirmPassword
      });
      message.success('用户密码重置成功');
      setResetModalVisible(false);
      setResetTargetUser(null);
      resetForm.resetFields();
    } catch (error: unknown) {
      if (error && typeof error === 'object' && 'errorFields' in error) {
        return;
      }
      console.error('重置密码失败:', error);
      let errorMessage = '用户密码重置失败';
      if (error && typeof error === 'object' && 'response' in error) {
        const errorResponse = error as { response?: { data?: { message?: string; errorsMap?: Record<string, string> } } };
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
        if (errorResponse.response?.data?.errorsMap) {
          const map = errorResponse.response.data.errorsMap;
          Object.keys(map).forEach(field => {
            resetForm.setFields([{ name: field, errors: [map[field]] }]);
          });
        }
      }
      message.error(errorMessage);
    } finally {
      setResetLoading(false);
    }
  };

  // 编辑用户
  const handleEdit = (user: UserEntity) => {
    setEditingUser(user);
    setIsModalVisible(true);
  };

  // 创建用户
  const handleCreate = () => {
    setEditingUser(null);
    setFormKey(prev => prev + 1);
    setIsModalVisible(true);
  };

  // 批量删除
  const handleBatchDelete = () => {
    if (selectedUsers.length === 0) {
      message.warning('请选择要删除的用户');
      return;
    }
    
    let ids = '';
    for (let i = 0; i < selectedUsers.length; i++) {
      ids += selectedUsers[i].userIden.userDomain + '_' + selectedUsers[i].userIden.userId + ',';
    }
    if (ids.indexOf(',') !== -1) {
      ids = ids.slice(0, ids.length - 1);
    }
    
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedUsers.length} 个用户吗？`,
      onOk: async () => {
        try {
          const response = await deleteUserApi(ids);
          if (response.restCode === '200') {
            setSelectedUsers([]);
            setSelectedRowKeys([]);
            fetchUserList(searchForm);
            message.success('用户删除成功');
          } else {
            message.error('用户删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('用户删除失败');
        }
      }
    });
  };

  // 单个删除
  const handleDelete = (user: UserEntity) => {
    Modal.confirm({
      title: '确认删除',
      content: `是否删除用户(${user.realName})？`,
      onOk: async () => {
        try {
          const userId = user.userIden.userDomain + '_' + user.userIden.userId;
          const response = await deleteUserApi(userId);
          if (response.restCode === '200') {
            fetchUserList(searchForm);
            message.success('用户删除成功');
          } else {
            message.error('用户删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('用户删除失败');
        }
      }
    });
  };

  // 行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: UserEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedUsers(selectedRows);
    },
    getCheckboxProps: (record: UserEntity) => ({
      name: record.realName,
    }),
  };

  // 获取状态标签
  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      [UserStatus.ACTIVE]: { color: 'green', text: '正常' },
      [UserStatus.LOCK]: { color: 'red', text: '锁定' },
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  // 表格列定义
  const columns: ColumnsType<UserEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '用户域',
      dataIndex: ['userIden', 'userDomain'],
      key: 'userDomain',
      ellipsis: true,
      responsive: ['sm'],
      render: (userDomain: string) => {
        const domain = domainList.find(d => d.name === userDomain || d.id === userDomain);
        return domain ? domain.display : userDomain;
      },
      // 可以去掉width，让其自动适应
      // minWidth自定义class可以在table组件的columns上加className
    },
    {
      title: '登录名',
      dataIndex: 'loginName',
      key: 'loginName',
      ellipsis: true,
      responsive: ['sm'],
    },
    {
      title: '用户姓名',
      dataIndex: 'realName',
      key: 'realName',
      ellipsis: true,
      responsive: ['sm'],
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
      responsive: ['md'],
    },
    {
      title: '上次登录时间',
      dataIndex: 'latestLoginTime',
      key: 'latestLoginTime',
      ellipsis: true,
      responsive: ['md'],
      render: (time: string) => time || '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 60,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      // 操作列可给最小宽
      width: 330,
      render: (_, record: UserEntity) => (
        <Space size="small">
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            type="primary"
          >
            修改
          </Button>
          <Button
            size="small"
            icon={<ReloadOutlined />}
            onClick={() => handleResetPassword(record)}
            type="primary"
          >
            重置密码
          </Button>
          {record.status === UserStatus.LOCK ? (
            <Button
              size="small"
              icon={<UserAddOutlined />}
              onClick={() => handleActive(record)}
              type="primary"
            >
              启用
            </Button>
          ) : record.status === UserStatus.ACTIVE ? (
            <Button
              size="small"
              icon={<UserDeleteOutlined />}
              onClick={() => handleInactive(record)}
              type="default"
            >
              停用
            </Button>
          ) : null}
          <Button
            size="small"
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record)}
            type="primary"
            danger
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    // 先加载部门数据
    fetchPrepareData();
    // 然后加载用户数据
    fetchUserList();
  }, []);

  // 当部门数据加载完成后，确保树能正确显示
  useEffect(() => {
    if (deptTreeData.length > 0) {
      console.log('部门树数据已加载:', deptTreeData);
    } else {
      console.log('部门树数据为空，长度:', deptTreeData.length);
    }
  }, [deptTreeData]);

  // 监控userList变化，用于调试
  useEffect(() => {
    console.log('userList状态变化:', {
      length: userList.length,
      data: userList,
      total: total,
      loading: loading
    });
  }, [userList, total, loading]);

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      {/* 面包屑导航 */}
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
              )
            },
            {
              title: '用户管理'
            }
          ]}
        />
      </div>
      
      <Divider />

      <Row gutter={20} wrap={false}>
        {/* 部门树 */}
        <Col
          flex="0 0 300px"
          style={{ minWidth: 260, maxWidth: 350 }}
        >
          <Card title="部门列表" size="small" style={{ marginBottom: 16, height: '600px' }}>
            <Input
              placeholder="请输入部门名称"
              style={{ marginBottom: 16 }}
              allowClear
              prefix={<SearchOutlined />}
            />
            <div style={{ 
              height: '500px', 
              overflow: 'auto', 
              border: '1px solid #f0f0f0', 
              borderRadius: '6px', 
              padding: '12px', 
              background: '#fafafa'
            }}>
              {deptTreeData.length > 0 ? (
                <Tree
                  treeData={deptTreeData}
                  showLine={false}
                  defaultExpandAll
                  onSelect={(selectedKeys, info) => {
                    console.log('选中部门:', selectedKeys, info);
                    if (selectedKeys.length > 0) {
                      const deptId = selectedKeys[0] as string;
                      setSearchForm(prev => ({ ...prev, deptId }));
                      setCurrentPage(1);
                      fetchUserList({ ...searchForm, deptId, page: 1 });
                    }
                  }}
                  style={{
                    background: 'transparent'
                  }}
                />
              ) : (
                <div style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
                  <div>暂无部门数据</div>
                  <div style={{ fontSize: '12px', marginTop: '8px' }}>
                    部门数量: {deptTreeData.length}
                  </div>
                </div>
              )}
            </div>
          </Card>
        </Col>
        {/* 用户数据 */}
        <Col flex="1 1 0" style={{ minWidth: 0 }}>
          <Card>
            {/* 搜索表单 */}
            <Card size="small" style={{ marginBottom: 16 }}>
              <Form
                form={form}
                layout="inline"
                onFinish={handleSearch}
              >
                <Row gutter={[16, 16]} style={{ width: '100%' }}>
                  <Col xs={24} sm={12} md={6}>
                    <Form.Item name="userDomain" label="用户域">
                      <Select placeholder="请选择用户域" allowClear style={{ width: '100%' }}>
                        {domainList.map(domain => (
                          <Option key={domain.name} value={domain.name}>
                            {domain.display}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col xs={24} sm={12} md={6}>
                    <Form.Item name="loginName" label="登录名">
                      <Input placeholder="请输入登录名" />
                    </Form.Item>
                  </Col>
                  <Col xs={24} sm={12} md={6}>
                    <Form.Item name="realName" label="用户姓名">
                      <Input placeholder="请输入用户姓名" />
                    </Form.Item>
                  </Col>
                  <Col xs={24} sm={12} md={6}>
                    <Form.Item name="status" label="状态">
                      <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                        <Option value={UserStatus.ACTIVE}>正常</Option>
                        <Option value={UserStatus.LOCK}>锁定</Option>
                      </Select>
                    </Form.Item>
                  </Col>
                </Row>
                <Row>
                  <Col span={24} style={{ textAlign: 'left', marginTop: 26, marginLeft: 2, marginBottom: 10 }}>
                    <Space>
                      <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                        搜索
                      </Button>
                      <Button onClick={handleReset} icon={<ClearOutlined />}>
                        重置
                      </Button>
                    </Space>
                  </Col>
                </Row>
              </Form>
            </Card>

            {/* 操作按钮 */}
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', background: 'white', borderRadius: '8px', border: '1px solid #f0f0f0' }}>
              <Space>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
                  新增
                </Button>
                <Button 
                  type="primary" 
                  danger 
                  icon={<DeleteOutlined />} 
                  disabled={selectedUsers.length === 0}
                  onClick={handleBatchDelete}
                >
                  删除
                </Button>
              </Space>
              <div>
                已选择 {selectedUsers.length} 项
              </div>
            </div>

            {/* 用户表格 */}
            <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
              <Table
                columns={columns}
                dataSource={userList}
                loading={loading}
                rowKey={(record, index) => {
                  if (record?.userIden?.userDomain && record?.userIden?.userId) {
                    return `${record.userIden.userDomain}-${record.userIden.userId}`;
                  }
                  return `row-${index}`;
                }}
                rowSelection={rowSelection}
                pagination={false}
                size="small"
                locale={{
                  emptyText: userList.length === 0 && !loading ? '暂无数据' : undefined
                }}
              />
            </div>

            {/* 分页 */}
            <div style={{ marginTop: 16, textAlign: 'right', padding: '16px', background: 'white', borderRadius: '8px' }}>
              <Pagination
                current={currentPage}
                pageSize={pageSize}
                total={total}
                showSizeChanger
                showQuickJumper
                showTotal={(total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`}
                onChange={handlePageChange}
                onShowSizeChange={handlePageChange}
                pageSizeOptions={['10', '20', '30', '50']}
              />
            </div>
          </Card>
        </Col>
      </Row>

      {/* 用户表单模态框 */}
      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingUser(null);
        }}
        footer={null}
        width={800}
      >
        <UserForm
          key={editingUser ? `edit-${editingUser.userIden.userDomain}-${editingUser.userIden.userId}` : `new-${formKey}`}
          user={editingUser}
          deptTreeData={convertToTreeSelectData(deptTreeData)}
          roleList={roleList}
          postList={postList}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingUser(null);
            fetchUserList(searchForm);
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingUser(null);
          }}
        />
      </Modal>

      <Modal
        title={`重置密码${resetTargetUser ? ` - ${resetTargetUser.realName}` : ''}`}
        open={resetModalVisible}
        onCancel={() => {
          setResetModalVisible(false);
          setResetTargetUser(null);
          resetForm.resetFields();
        }}
        onOk={handleResetPwdSubmit}
        confirmLoading={resetLoading}
        okText="确定"
        cancelText="取消"
      >
        <Form
          form={resetForm}
          layout="vertical"
        >
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6个字符' }
            ]}
          >
            <Input.Password placeholder="请输入新密码" maxLength={50} />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认密码"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请再次输入新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次密码不一致'));
                }
              })
            ]}
          >
            <Input.Password placeholder="请再次输入新密码" maxLength={50} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserList;