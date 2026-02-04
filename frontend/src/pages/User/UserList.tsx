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
  TreeSelect,
  Typography
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  UserDeleteOutlined,
  UserAddOutlined,
  UserOutlined,
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
  DeleteOutlined,
  UploadOutlined,
  DownloadOutlined
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
  resetPasswordApi,
  importUserApi,
  exportUserApi,
  exportAllUserApi
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
const { Title } = Typography;

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
  const [importLoading, setImportLoading] = useState(false);

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

      // response 已经是 response.data (UserListResponse 即 QueryResultSet<UserEntity>)
      // 由 user.ts 中的 getUserListApi 处理: return response.data
      const list = response.records || [];
      const total = response.totalCount || 0;

      setUserList(list.map((item, index) => ({ ...item, index })));
      setTotal(total);
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
    const userId = user.id;
    if (!userId) return;
    Modal.confirm({
      title: '确认启用',
      content: `确定要启用用户 ${user.realName} 吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const result = await activeUserApi(userId);
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
    const userId = user.id;
    if (!userId) return;
    Modal.confirm({
      title: '确认停用',
      content: `确定要停用用户 ${user.realName} 吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const result = await inactiveUserApi(userId);
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
    if (!resetTargetUser) return;
    const userId = resetTargetUser.id;
    const userDomain = resetTargetUser.userDomain;

    if (!userId || !userDomain) {
      return;
    }
    try {
      const values = await resetForm.validateFields();
      setResetLoading(true);
      await resetPasswordApi({
        userDomain,
        id: userId,
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

    const ids = selectedUsers
      .map(user => user.id)
      .filter(Boolean)
      .join(',');

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
    const userId = user.id;

    if (!userId) return;

    Modal.confirm({
      title: '确认删除',
      content: `是否删除用户(${user.realName})？`,
      onOk: async () => {
        try {
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

  // 导入用户
  const handleImport = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.xls,.xlsx';
    input.onchange = async (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (!file) {
        return;
      }
      setImportLoading(true);
      try {
        const response = await importUserApi(file);
        if (response.restCode === '200' || response.restCode === '0') {
          message.success('用户导入成功');
          fetchUserList(searchForm);
        } else {
          message.error(response.message || '用户导入失败');
        }
      } catch (error: any) {
        console.error('导入失败:', error);
        const errorMessage = error?.response?.data?.message || '用户导入失败';
        message.error(errorMessage);
      } finally {
        setImportLoading(false);
      }
    };
    input.click();
  };

  // 导出用户（选中）
  const handleExport = async () => {
    if (selectedUsers.length === 0) {
      message.warning('请选择要导出的用户');
      return;
    }
    try {
      const ids = selectedUsers
        .map(user => user.id)
        .filter(Boolean)
        .join(',');
      await exportUserApi(ids);
      message.success('用户导出成功');
    } catch (error) {
      console.error('导出失败:', error);
      message.error('用户导出失败');
    }
  };

  // 导出所有用户
  const handleExportAll = async () => {
    try {
      await exportAllUserApi(searchForm);
      message.success('用户导出成功');
    } catch (error) {
      console.error('导出失败:', error);
      message.error('用户导出失败');
    }
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
      [UserStatus.INACTIVE]: { color: 'orange', text: '停用' },
      [UserStatus.LOCKED]: { color: 'red', text: '锁定' },
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  const columns: ColumnsType<UserEntity> = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 60,
      render: (value: number) => (currentPage - 1) * pageSize + value + 1
    },
    {
      title: '用户域',
      dataIndex: 'userDomain',
      key: 'userDomain',
      ellipsis: true,
      responsive: ['sm'],
      render: (userDomain: string) => {
        const domain = domainList.find(d => d.name === userDomain || d.id === userDomain);
        return domain ? domain.display : userDomain;
      },
    },
    {
      title: '登录名',
      dataIndex: 'loginName',
      key: 'loginName',
      ellipsis: true,
      responsive: ['sm'],
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      key: 'roleName',
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
      title: '部门',
      dataIndex: 'deptName',
      key: 'deptName',
      ellipsis: true,
      responsive: ['md'],
      render: (text) => text || '-'
    },
    {
      title: '上次登录时间',
      dataIndex: 'latestLoginTime',
      key: 'latestLoginTime',
      ellipsis: true,
      responsive: ['md'],
      render: (time: string) => <span style={{ color: '#8c8c8c' }}>{time || '-'}</span>
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 330,
      render: (_, record: UserEntity) => (
        <Space size="small">
          <Button
            size="small"
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            修改
          </Button>
          <Button
            size="small"
            type="link"
            icon={<ReloadOutlined />}
            onClick={() => handleResetPassword(record)}
          >
            重置密码
          </Button>
          {record.status === UserStatus.LOCKED || record.status === UserStatus.INACTIVE ? (
            <Button
              size="small"
              type="link"
              icon={<UserAddOutlined />}
              onClick={() => handleActive(record)}
              style={{ color: '#52c41a' }}
            >
              启用
            </Button>
          ) : (
            <Button
              size="small"
              type="link"
              danger
              icon={<UserDeleteOutlined />}
              onClick={() => handleInactive(record)}
            >
              停用
            </Button>
          )}
          <Button
            size="small"
            type="link"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record)}
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    fetchPrepareData();
    fetchUserList();
  }, []);

  return (
    <div className="fade-in" style={{ padding: '0' }}>

      <Card className="modern-card" title="用户管理" bordered={false}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSearch}
          className="search-form"
        >
          <Row gutter={24}>
            <Col xs={24} sm={8} md={6}>
              <Form.Item name="deptId" label="部门">
                <TreeSelect
                  treeData={convertToTreeSelectData(deptTreeData)}
                  placeholder="选择部门"
                  allowClear
                  treeDefaultExpandAll
                  style={{ width: '100%' }}
                  listHeight={400}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8} md={6}>
              <Form.Item name="loginName" label="登录者">
                <Input placeholder="请输入登录名" allowClear prefix={<UserOutlined style={{ color: '#d9d9d9' }} />} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8} md={6}>
              <Form.Item name="realName" label="用户姓名">
                <Input placeholder="请输入用户姓名" allowClear prefix={<UserOutlined style={{ color: '#d9d9d9' }} />} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={24} md={6} style={{ display: 'flex', alignItems: 'flex-end', paddingBottom: 24 }}>
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

        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增用户
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              disabled={selectedUsers.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除
            </Button>
            <Button
              icon={<UploadOutlined />}
              onClick={handleImport}
              loading={importLoading}
            >
              导入
            </Button>
            <Button
              icon={<DownloadOutlined />}
              onClick={handleExportAll}
            >
              导出
            </Button>
          </Space>
        </div>

        <div className="modern-table">
          <Table
            columns={columns}
            dataSource={userList}
            loading={loading}
            rowKey={(record) => {
              if (record.id && record.userDomain) {
                return `${record.userDomain}-${record.id}`;
              }
              return `row-${record.index}`;
            }}
            rowSelection={rowSelection}
            pagination={{
              current: currentPage,
              pageSize: pageSize,
              total: total,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`,
              onChange: handlePageChange,
              onShowSizeChange: handlePageChange,
              pageSizeOptions: ['10', '20', '30', '50']
            }}
            size="middle"
            locale={{
              emptyText: userList.length === 0 && !loading ? '暂无数据' : undefined
            }}
          />
        </div>
      </Card>

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
          key={editingUser ? `edit-${editingUser.userDomain}-${editingUser.id}` : `new-${formKey}`}
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
        <Form autoComplete="off"
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
            <Input.Password placeholder="请输入新密码" maxLength={50} allowClear />
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
            <Input.Password placeholder="请再次输入新密码" maxLength={50} allowClear />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserList;