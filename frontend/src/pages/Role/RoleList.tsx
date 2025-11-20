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
  Breadcrumb,
  Divider
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  deleteRoleApi,
  getMenuTreeApi,
  getRoleListApi
} from '../../api/role';
import {
  RoleStatus,
  SystemConfig,
  type RoleEntity,
  type RoleSearchForm,
  type MenuTreeNode,
  type RoleListResponse
} from '../../types';
import RoleForm from './RoleForm';

const { Option } = Select;

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
  // 模态框状态
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleEntity | null>(null);
  
  // 菜单树数据
  const [menuOptions, setMenuOptions] = useState<MenuTreeNode[]>([]);

  // 获取角色列表
  const fetchRoleList = async (searchParams?: RoleSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response: RoleListResponse = await getRoleListApi(params);

      if (response.restCode === '200') {
        setRoleList(response.data.records || []);
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
        setMenuOptions([]);
      }
    } catch (error) {
      console.error('获取菜单树失败:', error);
      message.error('获取菜单树失败');
      setMenuOptions([]);
    }
  };

  // 搜索
  const handleSearch = (values: RoleSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchRoleList(values, 1);
  };

  // 重置搜索
  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchRoleList(emptyForm, 1);
  };

  // 分页变化
  const handlePageChange = (page: number, size?: number) => {
    const newPageSize = size ?? pageSize;
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchRoleList(undefined, page, newPageSize);
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
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedRoles.length} 个角色吗？`,
      onOk: async () => {
        try {
          const validIds = selectedRoles
            .map(role => role.id)
            .filter((id): id is string => id !== undefined);

          if (validIds.length === 0) {
            message.error('选中的角色中没有有效的ID');
            return;
          }

          console.log('开始批量删除角色:', validIds);
          const ids = validIds.join(',');
          const response = await deleteRoleApi(ids);
          if (response.restCode === '200') {
            message.success('批量删除成功');
            setSelectedRoles([]);
            setSelectedRowKeys([]);
            fetchRoleList();
          } else {
            message.error(response.message || '批量删除失败');
          }
        } catch (error) {
          console.error('批量删除失败:', error);
          message.error('批量删除失败');
        }
      }
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
      content: `确定要删除角色 "${role.display}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      maskClosable: true,
      centered: true,
      getContainer: () => document.getElementById('root') || document.body,
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
      }
    });
  };

  // 行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: RoleEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedRoles(selectedRows);
    },
    getCheckboxProps: (record: RoleEntity) => ({
      name: record.display,
    }),
  };

  // 获取状态标签
  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      [RoleStatus.ACTIVE]: { color: 'green', text: '正常' },
      [RoleStatus.INACTIVE]: { color: 'red', text: '停用' }
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  // 获取系统配置标签
  const getSystemTag = (system: string) => {
    const systemMap: Record<string, { color: string; text: string }> = {
      [SystemConfig.YES]: { color: 'blue', text: '是' },
      [SystemConfig.NO]: { color: 'default', text: '否' }
    };
    const systemInfo = systemMap[system] || { color: 'default', text: system };
    return <Tag color={systemInfo.color}>{systemInfo.text}</Tag>;
  };

  // 表格列定义
  const columns: ColumnsType<RoleEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '编号',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true
    },
    {
      title: '显示名称',
      dataIndex: 'display',
      key: 'display',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      width: 120,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '系统配置',
      dataIndex: 'system',
      key: 'system',
      width: 120,
      align: 'center',
      render: (system: string) => getSystemTag(system)
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record: RoleEntity) => (
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
            icon={<DeleteOutlined />}
            onClick={() => {
              handleDelete(record);
            }}
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
    fetchRoleList();
    fetchMenuTree();
  }, []);

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      {/* 面包屑导航 */}
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
            title: '角色管理'
          }
        ]}
      />
      
      <Divider />

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
                <Form.Item name="name" label="编号">
                  <Input placeholder="请输入编号" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="display" label="显示名称">
                  <Input placeholder="请输入显示名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                    <Option value={RoleStatus.ACTIVE}>正常</Option>
                    <Option value={RoleStatus.INACTIVE}>停用</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="system" label="系统配置">
                  <Select placeholder="请选择系统配置" allowClear style={{ width: '100%' }}>
                    <Option value={SystemConfig.YES}>是</Option>
                    <Option value={SystemConfig.NO}>否</Option>
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
              disabled={selectedRoles.length === 0}
              onClick={() => {
                handleBatchDelete();
              }}
            >
              删除
            </Button>
          </Space>
          <div>
            已选择 {selectedRoles.length} 项
          </div>
        </div>

        {/* 角色表格 */}
        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={roleList}
            loading={loading}
            rowKey={(record) => record.id || `role-${record.name}`}
            rowSelection={rowSelection}
            pagination={false}
            scroll={{ x: 800 }}
            size="small"
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
