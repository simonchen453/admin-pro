import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Select,
  Tag,
  message,
  Modal,
  Row,
  Col,
  TreeSelect,
  Typography,
  Dropdown,
  Avatar,
  Segmented,
  Tooltip,
  Popover,
  Checkbox,
  Badge,
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
  DownloadOutlined,
  DownOutlined,
  ColumnHeightOutlined,
  SettingOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  InfoCircleFilled,
  ApartmentOutlined,
  ClockCircleOutlined,
  LockOutlined,
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
  exportAllUserApi,
} from '../../api/user';
import type {
  UserEntity,
  UserSearchForm,
  RoleEntity,
  PostEntity,
} from '../../types';
import { UserStatus } from '../../types';
import UserForm from './UserForm';
import './UserList.css';

const { Text } = Typography;

// 角色语义与色彩映射
const ROLE_NAME_MAP: Record<string, { label: string; color: string }> = {
  SYS_SUPER_ADMIN_ROLE: { label: '超级管理员', color: 'purple' },
  SYS_ADMIN_ROLE: { label: '系统管理员', color: 'blue' },
  NORMAL_USER: { label: '普通用户', color: 'cyan' },
};

// 头像色彩计算
const getAvatarColor = (str: string) => {
  const colors = ['#1677ff', '#722ed1', '#13c2c2', '#52c41a', '#fa8c16', '#eb2f96', '#2f54eb'];
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  return colors[Math.abs(hash) % colors.length];
};

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

  // 现代表格增强状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [statusTab, setStatusTab] = useState<string>('ALL');
  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'userInfo',
    'userDomain',
    'deptName',
    'roleName',
    'latestLoginTime',
    'status',
    'action',
  ]);

  // 全屏容器引用
  const tableContainerRef = useRef<HTMLDivElement>(null);

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

  interface DeptTreeApiResponse {
    id: string;
    label: string;
    children?: DeptTreeApiResponse[];
  }

  const convertDeptTree = (data: DeptTreeApiResponse[]): DeptTreeNode[] => {
    if (!data || data.length === 0) {
      return [];
    }
    return data.map((item) => ({
      key: item.id,
      title: item.label,
      children: item.children ? convertDeptTree(item.children) : undefined,
    }));
  };

  interface DeptTreeSelectItem {
    key: string;
    title: string;
    value: string;
    children?: DeptTreeSelectItem[];
  }

  const convertToTreeSelectData = (treeData: DeptTreeNode[]): DeptTreeSelectItem[] => {
    return treeData.map((node) => ({
      key: node.key,
      title: node.title,
      value: node.key,
      children: node.children ? convertToTreeSelectData(node.children) : undefined,
    }));
  };

  // 获取准备数据
  const fetchPrepareData = async () => {
    try {
      const [deptData, prepareData, domains] = await Promise.all([
        getDeptTreeSelectApi(),
        getUserPrepareDataApi(),
        getDomainListApi(),
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
        pageSize: params.pageSize ?? pageSize,
      });

      const list = response.records || [];
      const totalCount = response.totalCount || 0;

      setUserList(list);
      setTotal(totalCount);
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
    const query = {
      ...values,
      status: statusTab === 'ALL' ? values.status : statusTab,
    };
    setSearchForm(query);
    setCurrentPage(1);
    fetchUserList(query);
  };

  // 重置搜索
  const handleReset = () => {
    form.resetFields();
    setStatusTab('ALL');
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

  // 切换状态 Tab
  const handleStatusTabChange = (val: string | number) => {
    const tab = String(val);
    setStatusTab(tab);
    setCurrentPage(1);
    const newStatus = tab === 'ALL' ? undefined : tab;
    const newForm = { ...searchForm, status: newStatus };
    setSearchForm(newForm);
    fetchUserList(newForm);
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
            message.success('用户启用成功');
            fetchUserList(searchForm);
          } else {
            message.error(result.message || '用户启用失败');
          }
        } catch {
          message.error('用户启用失败');
        }
      },
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
        } catch {
          message.error('用户停用失败');
        }
      },
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
        confirmPassword: values.confirmPassword,
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
        const errorResponse = error as {
          response?: { data?: { message?: string; errorsMap?: Record<string, string> } };
        };
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
        if (errorResponse.response?.data?.errorsMap) {
          const map = errorResponse.response.data.errorsMap;
          Object.keys(map).forEach((field) => {
            resetForm.setFields([{ name: field, errors: [map[field]] }]);
          });
        }
      }
      message.error(errorMessage);
    } finally {
      setResetLoading(false);
    }
  };

  // 编辑与创建
  const handleEdit = (user: UserEntity) => {
    setEditingUser(user);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingUser(null);
    setFormKey((prev) => prev + 1);
    setIsModalVisible(true);
  };

  // 批量删除
  const handleBatchDelete = () => {
    if (selectedUsers.length === 0) {
      message.warning('请选择要删除的用户');
      return;
    }

    const ids = selectedUsers.map((user) => user.id).filter(Boolean).join(',');

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedUsers.length} 个用户吗？删除后不可恢复。`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteUserApi(ids);
          if (response.restCode === '200') {
            setSelectedUsers([]);
            setSelectedRowKeys([]);
            fetchUserList(searchForm);
            message.success('用户批量删除成功');
          } else {
            message.error(response.message || '用户删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('用户删除失败');
        }
      },
    });
  };

  // 单个删除
  const handleDelete = (user: UserEntity) => {
    const userId = user.id;
    if (!userId) return;

    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除用户「${user.realName}」？`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteUserApi(userId);
          if (response.restCode === '200') {
            fetchUserList(searchForm);
            message.success('用户删除成功');
          } else {
            message.error(response.message || '用户删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('用户删除失败');
        }
      },
    });
  };

  // 导入用户
  const handleImport = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.xls,.xlsx';
    input.onchange = async (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (!file) return;
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

  // 导出选中
  const handleExport = async () => {
    if (selectedUsers.length === 0) {
      message.warning('请选择要导出的用户');
      return;
    }
    try {
      const ids = selectedUsers.map((user) => user.id).filter(Boolean).join(',');
      await exportUserApi(ids);
      message.success('用户导出成功');
    } catch (error) {
      console.error('导出失败:', error);
      message.error('用户导出失败');
    }
  };

  // 导出所有
  const handleExportAll = async () => {
    try {
      await exportAllUserApi(searchForm);
      message.success('已启动全量导出');
    } catch (error) {
      console.error('导出失败:', error);
      message.error('用户导出失败');
    }
  };

  // 行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[], rows: UserEntity[]) => {
      setSelectedRowKeys(keys);
      setSelectedUsers(rows);
    },
  };

  // 角色语义标签解析
  const renderRoleTag = (code?: string) => {
    if (!code) return <span style={{ color: 'var(--ap-text-3)' }}>-</span>;
    const mapped = ROLE_NAME_MAP[code];
    if (mapped) {
      return <Tag color={mapped.color}>{mapped.label}</Tag>;
    }
    const fromList = roleList.find((r) => r.code === code || r.name === code || r.display === code);
    if (fromList) {
      return <Tag color="blue">{fromList.display || fromList.name}</Tag>;
    }
    return <Tag>{code}</Tag>;
  };

  // 状态解析
  const renderStatusBadge = (status: string) => {
    if (status === UserStatus.ACTIVE || status === '1') {
      return <Badge status="success" text="正常" />;
    }
    if (status === UserStatus.LOCKED) {
      return <Badge status="error" text="锁定" />;
    }
    return <Badge status="warning" text="停用" />;
  };

  // 全量表格列定义（支持列配置过滤）
  const allColumns: ColumnsType<UserEntity> = useMemo(
    () => [
      {
        title: '用户信息',
        key: 'userInfo',
        width: 240,
        render: (_, record: UserEntity) => {
          const name = record.realName || record.loginName || 'User';
          const initial = name.charAt(0).toUpperCase();
          const avatarBg = getAvatarColor(record.loginName || name);

          return (
            <div className="crud-user-cell">
              <Avatar
                style={{ backgroundColor: avatarBg, verticalAlign: 'middle' }}
                size={38}
                className="crud-user-avatar"
              >
                {initial}
              </Avatar>
              <div className="crud-user-meta">
                <span className="crud-user-name">{name}</span>
                <span className="crud-user-login">@{record.loginName}</span>
              </div>
            </div>
          );
        },
      },
      {
        title: '用户域',
        dataIndex: 'userDomain',
        key: 'userDomain',
        width: 140,
        responsive: ['sm'],
        render: (userDomain: string) => {
          const domain = domainList.find((d) => d.name === userDomain || d.id === userDomain);
          return <Tag color="default">{domain ? domain.display : userDomain}</Tag>;
        },
      },
      {
        title: '所属部门',
        dataIndex: 'deptName',
        key: 'deptName',
        width: 160,
        responsive: ['md'],
        render: (deptName: string) =>
          deptName ? (
            <Space size={4}>
              <ApartmentOutlined style={{ color: '#1677ff' }} />
              <span>{deptName}</span>
            </Space>
          ) : (
            <span style={{ color: 'var(--ap-text-3)' }}>-</span>
          ),
      },
      {
        title: '系统角色',
        dataIndex: 'roleName',
        key: 'roleName',
        width: 150,
        responsive: ['sm'],
        render: (roleName: string) => renderRoleTag(roleName),
      },
      {
        title: '上次登录时间',
        dataIndex: 'latestLoginTime',
        key: 'latestLoginTime',
        width: 180,
        responsive: ['lg'],
        render: (time: string) =>
          time ? (
            <Space size={4} style={{ color: 'var(--ap-text-2)', fontSize: 13 }}>
              <ClockCircleOutlined style={{ color: 'var(--ap-text-3)' }} />
              <span>{time}</span>
            </Space>
          ) : (
            <span style={{ color: 'var(--ap-text-3)' }}>从未登录</span>
          ),
      },
      {
        title: '账号状态',
        dataIndex: 'status',
        key: 'status',
        width: 100,
        render: (status: string) => renderStatusBadge(status),
      },
      {
        title: '操作',
        key: 'action',
        fixed: 'right',
        width: 130,
        render: (_, record: UserEntity) => (
          <Space size={2}>
            <Button
              size="small"
              type="link"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              修改
            </Button>
            <Dropdown
              menu={{
                items: [
                  {
                    key: 'pwd',
                    icon: <LockOutlined />,
                    label: '重置密码',
                    onClick: () => handleResetPassword(record),
                  },
                  record.status === UserStatus.LOCKED || record.status === UserStatus.INACTIVE
                    ? {
                        key: 'active',
                        icon: <UserAddOutlined />,
                        label: '启用账号',
                        onClick: () => handleActive(record),
                      }
                    : {
                        key: 'inactive',
                        icon: <UserDeleteOutlined />,
                        label: '停用账号',
                        danger: true,
                        onClick: () => handleInactive(record),
                      },
                  { type: 'divider' },
                  {
                    key: 'delete',
                    icon: <DeleteOutlined />,
                    label: '删除用户',
                    danger: true,
                    onClick: () => handleDelete(record),
                  },
                ],
              }}
              trigger={['click']}
            >
              <Button size="small" type="link">
                更多 <DownOutlined style={{ fontSize: 10 }} />
              </Button>
            </Dropdown>
          </Space>
        ),
      },
    ],
    [domainList, roleList]
  );

  // 过滤展示的列
  const columns = useMemo(() => {
    return allColumns.filter((col) => !col.key || visibleColumns.includes(col.key as string));
  }, [allColumns, visibleColumns]);

  // 列配置 Popover 内容
  const columnSettingContent = (
    <div style={{ width: 180 }}>
      <div className="crud-col-popover-title">
        <span>展示列配置</span>
        <Button
          type="link"
          size="small"
          style={{ padding: 0 }}
          onClick={() =>
            setVisibleColumns([
              'userInfo',
              'userDomain',
              'deptName',
              'roleName',
              'latestLoginTime',
              'status',
              'action',
            ])
          }
        >
          重置
        </Button>
      </div>
      {allColumns.map((col) => {
        if (!col.key || col.key === 'action') return null;
        const key = col.key as string;
        const checked = visibleColumns.includes(key);
        return (
          <div key={key} className="crud-col-item">
            <Checkbox
              checked={checked}
              onChange={(e) => {
                if (e.target.checked) {
                  setVisibleColumns([...visibleColumns, key]);
                } else {
                  setVisibleColumns(visibleColumns.filter((k) => k !== key));
                }
              }}
            >
              {typeof col.title === 'string' ? col.title : key}
            </Checkbox>
          </div>
        );
      })}
    </div>
  );

  useEffect(() => {
    fetchPrepareData();
    fetchUserList();
  }, []);

  return (
    <div className="crud-container fade-in">
      {/* 顶部独立查询筛选卡片 */}
      <div className="crud-query-card">
        {/* 快捷状态分段控制器 */}
        <div className="crud-filter-tabs">
          <Segmented
            value={statusTab}
            onChange={handleStatusTabChange}
            options={[
              { label: '全部用户', value: 'ALL' },
              { label: '正常活跃', value: UserStatus.ACTIVE },
              { label: '已停用', value: UserStatus.INACTIVE },
            ]}
          />
        </div>

        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={8} md={6}>
              <Form.Item name="deptId" label="部门">
                <TreeSelect
                  treeData={convertToTreeSelectData(deptTreeData)}
                  placeholder="选择部门"
                  allowClear
                  treeDefaultExpandAll
                  style={{ width: '100%' }}
                  listHeight={320}
                />
              </Form.Item>
            </Col>

            <Col xs={24} sm={8} md={6}>
              <Form.Item name="loginName" label="登录者">
                <Input
                  placeholder="请输入登录名"
                  allowClear
                  prefix={<UserOutlined style={{ color: 'var(--ap-text-3)' }} />}
                />
              </Form.Item>
            </Col>

            <Col xs={24} sm={8} md={6}>
              <Form.Item name="realName" label="用户姓名">
                <Input
                  placeholder="请输入用户姓名"
                  allowClear
                  prefix={<UserOutlined style={{ color: 'var(--ap-text-3)' }} />}
                />
              </Form.Item>
            </Col>

            <Col xs={24} sm={24} md={6} style={{ display: 'flex', alignItems: 'flex-end', paddingBottom: 16 }}>
              <Space size={8}>
                <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                  查询
                </Button>
                <Button onClick={handleReset} icon={<ClearOutlined />}>
                  重置
                </Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </div>

      {/* 主数据表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        {/* 顶部操作工具栏 (ProTable Toolbar) */}
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
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
            <Button icon={<UploadOutlined />} onClick={handleImport} loading={importLoading}>
              导入
            </Button>
            <Button icon={<DownloadOutlined />} onClick={handleExportAll}>
              导出全部
            </Button>
          </div>

          <div className="crud-toolbar-right">
            {/* 1. 局部刷新 */}
            <Tooltip title="刷新数据">
              <Button
                type="text"
                className="crud-tool-btn"
                icon={<ReloadOutlined spin={loading} />}
                onClick={() => fetchUserList(searchForm)}
              />
            </Tooltip>

            {/* 2. 表格密度 */}
            <Dropdown
              menu={{
                items: [
                  { key: 'large', label: '宽松' },
                  { key: 'middle', label: '默认' },
                  { key: 'small', label: '紧凑' },
                ],
                selectedKeys: [tableSize],
                onClick: (e) => setTableSize(e.key as 'small' | 'middle' | 'large'),
              }}
              trigger={['click']}
            >
              <Tooltip title="表格密度">
                <Button type="text" className="crud-tool-btn" icon={<ColumnHeightOutlined />} />
              </Tooltip>
            </Dropdown>

            {/* 3. 列设置 */}
            <Popover content={columnSettingContent} trigger="click" placement="bottomRight">
              <Tooltip title="列显隐设置">
                <Button type="text" className="crud-tool-btn" icon={<SettingOutlined />} />
              </Tooltip>
            </Popover>

            {/* 4. 全屏切换 */}
            <Tooltip title={isFullscreen ? '退出全屏' : '全屏聚焦'}>
              <Button
                type="text"
                className="crud-tool-btn"
                icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
                onClick={() => setIsFullscreen(!isFullscreen)}
              />
            </Tooltip>
          </div>
        </div>

        {/* 批量操作提示浮条 (当有勾选时平滑展示) */}
        {selectedRowKeys.length > 0 && (
          <div className="crud-selection-bar">
            <div className="crud-selection-bar-text">
              <InfoCircleFilled />
              <span>
                已选择 <strong style={{ color: '#1677ff' }}>{selectedRowKeys.length}</strong> 项
              </span>
              <Button
                type="link"
                size="small"
                style={{ padding: 0 }}
                onClick={() => {
                  setSelectedRowKeys([]);
                  setSelectedUsers([]);
                }}
              >
                清空已选
              </Button>
            </div>
            <Space size={8}>
              <Button size="small" icon={<DownloadOutlined />} onClick={handleExport}>
                导出选中项
              </Button>
              <Button
                size="small"
                danger
                icon={<DeleteOutlined />}
                onClick={handleBatchDelete}
              >
                批量删除
              </Button>
            </Space>
          </div>
        )}

        {/* 数据表格 */}
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
            showTotal: (totalCount, range) =>
              `第 ${range[0]}-${range[1]} 条 / 共 ${totalCount} 条数据`,
            onChange: handlePageChange,
            onShowSizeChange: handlePageChange,
            pageSizeOptions: ['10', '20', '30', '50'],
          }}
          size={tableSize}
          locale={{
            emptyText: userList.length === 0 && !loading ? '暂无匹配用户' : undefined,
          }}
        />
      </div>

      {/* 新增/编辑用户模态框 */}
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

      {/* 重置密码模态框 */}
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
        <Form autoComplete="off" form={resetForm} layout="vertical">
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6个字符' },
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
                },
              }),
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