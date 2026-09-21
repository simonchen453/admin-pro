import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Tag,
  message,
  Modal,
  Row,
  Col,
  Segmented,
  Tooltip,
  Dropdown,
  Popover,
  Checkbox,
  Badge,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  ReloadOutlined,
  ColumnHeightOutlined,
  SettingOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  InfoCircleFilled,
  SafetyCertificateOutlined,
  KeyOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  deleteRoleApi,
  getMenuTreeApi,
  getRoleListApi,
} from '../../api/role';
import {
  RoleStatus,
  SystemConfig,
  type RoleEntity,
  type RoleSearchForm,
  type MenuTreeNode,
  type RoleListResponse,
} from '../../types';
import RoleForm from './RoleForm';
import '../../styles/crud.css';

const RoleList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [roleList, setRoleList] = useState<RoleEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<RoleSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedRoles, setSelectedRoles] = useState<RoleEntity[]>([]);

  // 现代 ProTable 增强状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [statusTab, setStatusTab] = useState<string>('ALL');
  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'roleInfo',
    'system',
    'status',
    'action',
  ]);

  const tableContainerRef = useRef<HTMLDivElement>(null);

  // 模态框状态
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleEntity | null>(null);
  const [menuOptions, setMenuOptions] = useState<MenuTreeNode[]>([]);

  // 获取角色列表
  const fetchRoleList = async (searchParams?: RoleSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize,
      };

      const response: RoleListResponse = await getRoleListApi(params);

      if (response.restCode === '200') {
        setRoleList((response.data.records || []).map((item: any, index: number) => ({ ...item, index })));
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取角色列表失败');
        setRoleList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取角色列表失败:', error);
      message.error('获取角色列表失败');
      setRoleList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  // 获取菜单树
  const fetchMenuTree = async () => {
    try {
      const response = await getMenuTreeApi();
      if (response.restCode === '200') {
        setMenuOptions(response.data || []);
      } else {
        message.error(response.message || '获取菜单树失败');
      }
    } catch (error) {
      console.error('获取菜单树失败:', error);
      message.error('获取菜单树失败');
    }
  };

  // 搜索
  const handleSearch = (values: RoleSearchForm) => {
    const query = {
      ...values,
      status: statusTab === 'ALL' ? values.status : statusTab,
    };
    setSearchForm(query);
    setCurrentPage(1);
    fetchRoleList(query, 1);
  };

  // 重置搜索
  const handleReset = () => {
    form.resetFields();
    setStatusTab('ALL');
    setSearchForm({});
    setCurrentPage(1);
    fetchRoleList({}, 1);
  };

  // 状态 Tab 切换
  const handleStatusTabChange = (val: string | number) => {
    const tab = String(val);
    setStatusTab(tab);
    setCurrentPage(1);
    const newStatus = tab === 'ALL' ? undefined : tab;
    const newForm = { ...searchForm, status: newStatus };
    setSearchForm(newForm);
    fetchRoleList(newForm, 1);
  };

  // 分页变化
  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchRoleList(searchForm, page, size || pageSize);
  };

  // 编辑角色
  const handleEdit = (role: RoleEntity) => {
    setEditingRole(role);
    setIsModalVisible(true);
  };

  // 创建角色
  const handleCreate = () => {
    setEditingRole(null);
    setIsModalVisible(true);
  };

  // 批量删除
  const handleBatchDelete = () => {
    if (selectedRoles.length === 0) {
      message.warning('请选择要删除的角色');
      return;
    }

    const ids = selectedRoles
      .map((role) => role.id)
      .filter(Boolean)
      .join(',');

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedRoles.length} 个角色吗？删除后不可恢复。`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteRoleApi(ids);
          if (response.restCode === '200') {
            message.success('角色批量删除成功');
            setSelectedRowKeys([]);
            setSelectedRoles([]);
            fetchRoleList();
          } else {
            message.error(response.message || '批量删除失败');
          }
        } catch (error) {
          console.error('批量删除失败:', error);
          message.error('批量删除失败');
        }
      },
    });
  };

  // 单个删除
  const handleDelete = (role: RoleEntity) => {
    if (!role.id) {
      message.error('角色ID不存在');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除角色「${role.display}」吗？`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      centered: true,
      onOk: async () => {
        try {
          const response = await deleteRoleApi(role.id!);
          if (response.restCode === '200') {
            message.success('删除成功');
            fetchRoleList();
          } else {
            message.error(response.message || '删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('删除失败');
        }
      },
    });
  };

  // 行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[], rows: RoleEntity[]) => {
      setSelectedRowKeys(keys);
      setSelectedRoles(rows);
    },
    getCheckboxProps: (record: RoleEntity) => ({
      name: record.display,
    }),
  };

  // 全量表格列定义
  const allColumns: ColumnsType<RoleEntity> = useMemo(
    () => [
      {
        title: '角色信息',
        key: 'roleInfo',
        width: 280,
        render: (_, record: RoleEntity) => (
          <div className="crud-user-cell">
            <div
              style={{
                width: 36,
                height: 36,
                borderRadius: 8,
                background: '#f0f5ff',
                color: '#1677ff',
                display: 'grid',
                placeItems: 'center',
                fontSize: 16,
                flex: 'none',
              }}
            >
              <KeyOutlined />
            </div>
            <div className="crud-user-meta">
              <span className="crud-user-name">{record.display}</span>
              <span className="crud-user-login">{record.name}</span>
            </div>
          </div>
        ),
      },
      {
        title: '角色类型',
        dataIndex: 'system',
        key: 'system',
        width: 140,
        align: 'center',
        render: (system: string) =>
          system === SystemConfig.YES || system === '1' ? (
            <Tag color="purple" icon={<SafetyCertificateOutlined />}>
              系统内置
            </Tag>
          ) : (
            <Tag color="blue">自定义角色</Tag>
          ),
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        align: 'center',
        width: 120,
        render: (status: string) =>
          status === RoleStatus.ACTIVE || status === '0' ? (
            <Badge status="success" text="正常启用" />
          ) : (
            <Badge status="error" text="已停用" />
          ),
      },
      {
        title: '操作',
        key: 'action',
        width: 160,
        fixed: 'right',
        render: (_, record: RoleEntity) => (
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
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleDelete(record)}
            >
              删除
            </Button>
          </Space>
        ),
      },
    ],
    []
  );

  const columns = useMemo(() => {
    return allColumns.filter((col) => !col.key || visibleColumns.includes(col.key as string));
  }, [allColumns, visibleColumns]);

  // 列配置 Popover
  const columnSettingContent = (
    <div style={{ width: 180 }}>
      <div className="crud-col-popover-title">
        <span>展示列配置</span>
        <Button
          type="link"
          size="small"
          style={{ padding: 0 }}
          onClick={() => setVisibleColumns(['roleInfo', 'system', 'status', 'action'])}
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
    fetchRoleList();
    fetchMenuTree();
  }, []);

  return (
    <div className="crud-container fade-in">
      {/* 顶部独立查询卡片 */}
      <div className="crud-query-card">
        <div className="crud-filter-tabs">
          <Segmented
            value={statusTab}
            onChange={handleStatusTabChange}
            options={[
              { label: '全部角色', value: 'ALL' },
              { label: '正常启用', value: RoleStatus.ACTIVE },
              { label: '已停用', value: RoleStatus.INACTIVE },
            ]}
          />
        </div>

        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={8} md={8}>
              <Form.Item name="name" label="角色标识/编号">
                <Input
                  placeholder="请输入角色编号 (如 SYS_ADMIN_ROLE)"
                  allowClear
                  prefix={<KeyOutlined style={{ color: 'var(--ap-text-3)' }} />}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8} md={8}>
              <Form.Item name="display" label="角色显示名称">
                <Input
                  placeholder="请输入显示名称 (如 管理员)"
                  allowClear
                  prefix={<SearchOutlined style={{ color: 'var(--ap-text-3)' }} />}
                />
              </Form.Item>
            </Col>

            <Col xs={24} sm={8} md={8} style={{ display: 'flex', alignItems: 'flex-end', paddingBottom: 16 }}>
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

      {/* 角色主数据表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        {/* 工具栏 */}
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增角色
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              disabled={selectedRoles.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除
            </Button>
          </div>

          <div className="crud-toolbar-right">
            <Tooltip title="刷新数据">
              <Button
                type="text"
                className="crud-tool-btn"
                icon={<ReloadOutlined spin={loading} />}
                onClick={() => fetchRoleList(searchForm)}
              />
            </Tooltip>

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

            <Popover content={columnSettingContent} trigger="click" placement="bottomRight">
              <Tooltip title="列显隐设置">
                <Button type="text" className="crud-tool-btn" icon={<SettingOutlined />} />
              </Tooltip>
            </Popover>

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

        {/* 批量操作浮条 */}
        {selectedRowKeys.length > 0 && (
          <div className="crud-selection-bar">
            <div className="crud-selection-bar-text">
              <InfoCircleFilled />
              <span>
                已选择 <strong style={{ color: '#1677ff' }}>{selectedRowKeys.length}</strong> 个角色
              </span>
              <Button
                type="link"
                size="small"
                style={{ padding: 0 }}
                onClick={() => {
                  setSelectedRowKeys([]);
                  setSelectedRoles([]);
                }}
              >
                清空已选
              </Button>
            </div>
            <Space size={8}>
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

        {/* 表格 */}
        <Table
          columns={columns}
          dataSource={roleList}
          loading={loading}
          rowKey={(record) => record.id || `role-${record.name}`}
          rowSelection={rowSelection}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (totalCount, range) =>
              `第 ${range[0]}-${range[1]} 条 / 共 ${totalCount} 条角色`,
            onChange: handlePageChange,
            onShowSizeChange: handlePageChange,
            pageSizeOptions: ['10', '20', '30', '50'],
          }}
          size={tableSize}
          locale={{
            emptyText: roleList.length === 0 && !loading ? '暂无匹配角色' : undefined,
          }}
        />
      </div>

      {/* 角色表单模态框 */}
      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingRole(null);
        }}
        footer={null}
        width={600}
        destroyOnHidden
      >
        <RoleForm
          role={editingRole}
          menuOptions={menuOptions}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingRole(null);
            fetchRoleList();
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingRole(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default RoleList;
