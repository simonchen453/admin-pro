import React, { useState, useEffect, useRef } from 'react';
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
  Badge,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  ExpandOutlined,
  CompressOutlined,
  ApartmentOutlined,
  ReloadOutlined,
  ColumnHeightOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getDeptListApi,
  getDeptDetailApi,
  deleteDeptApi,
  getDeptTreeSelectApi,
} from '../../api/dept';
import type {
  DeptEntity,
  DeptSearchForm,
} from '../../types';
import { DeptStatus } from '../../types';
import DeptForm from './DeptForm';
import '../../styles/crud.css';

const DeptList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [deptList, setDeptList] = useState<DeptEntity[]>([]);
  const [searchForm, setSearchForm] = useState<DeptSearchForm>({});

  // 现代 ProTable 状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [statusTab, setStatusTab] = useState<string>('ALL');
  const tableContainerRef = useRef<HTMLDivElement>(null);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingDept, setEditingDept] = useState<DeptEntity | null>(null);
  const [formKey, setFormKey] = useState(0);
  const [deptTreeOptions, setDeptTreeOptions] = useState<any[]>([]);
  const [expandedRowKeys, setExpandedRowKeys] = useState<React.Key[]>([]);

  const handleTree = (data: DeptEntity[], parentId: string | number = '0'): DeptEntity[] => {
    const tree: DeptEntity[] = [];
    data.forEach((item) => {
      const itemParentId = item.parentId || '0';
      const compareParentId = typeof parentId === 'string' ? parentId : String(parentId);
      if (itemParentId === compareParentId || (itemParentId === '0' && parentId === '0')) {
        const children = handleTree(data, item.id || '0');
        if (children.length > 0) {
          item.children = children;
        }
        tree.push(item);
      }
    });
    return tree;
  };

  const getAllRowKeys = (data: DeptEntity[]): React.Key[] => {
    const keys: React.Key[] = [];
    const traverse = (items: DeptEntity[]) => {
      items.forEach((item) => {
        if (item.id) {
          keys.push(item.id);
        }
        if (item.children && item.children.length > 0) {
          traverse(item.children);
        }
      });
    };
    traverse(data);
    return keys;
  };

  const fetchDeptList = async (params: DeptSearchForm = {}) => {
    setLoading(true);
    try {
      const response = await getDeptListApi(params);
      const list = response.data || [];
      const treeData = handleTree(list, '0');
      setDeptList(treeData);
      const allKeys = getAllRowKeys(treeData);
      setExpandedRowKeys(allKeys);
    } catch (error) {
      console.error('获取部门列表失败:', error);
      message.error('数据加载失败');
      setDeptList([]);
      setExpandedRowKeys([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchDeptTreeOptions = async () => {
    try {
      const response = await getDeptTreeSelectApi();
      if (response.restCode === '200' || response.restCode === '0') {
        const rootOption = {
          id: '0',
          label: '主部门',
          value: '0',
          children: response.data || [],
        };
        setDeptTreeOptions([rootOption]);
      } else {
        setDeptTreeOptions([]);
      }
    } catch (error) {
      console.error('获取部门树选项失败:', error);
      setDeptTreeOptions([]);
    }
  };

  const handleSearch = (values: DeptSearchForm) => {
    const query = {
      ...values,
      status: statusTab === 'ALL' ? values.status : statusTab,
    };
    setSearchForm(query);
    fetchDeptList(query);
  };

  const handleReset = () => {
    form.resetFields();
    setStatusTab('ALL');
    setSearchForm({});
    fetchDeptList({});
  };

  const handleStatusTabChange = (val: string | number) => {
    const tab = String(val);
    setStatusTab(tab);
    const newStatus = tab === 'ALL' ? undefined : tab;
    const newForm = { ...searchForm, status: newStatus };
    setSearchForm(newForm);
    fetchDeptList(newForm);
  };

  const handleCreate = () => {
    setEditingDept(null);
    setFormKey((prev) => prev + 1);
    fetchDeptTreeOptions();
    setIsModalVisible(true);
  };

  const handleAdd = (parentDept?: DeptEntity) => {
    setEditingDept(null);
    setFormKey((prev) => prev + 1);
    fetchDeptTreeOptions();
    setIsModalVisible(true);
    if (parentDept) {
      setTimeout(() => {
        setEditingDept({ parentId: parentDept.id } as DeptEntity);
      }, 0);
    }
  };

  const handleEdit = async (dept: DeptEntity) => {
    try {
      if (!dept.id) return;
      setLoading(true);
      const response = await getDeptDetailApi(dept.id);
      if (response.restCode === '200' || response.restCode === '0') {
        setEditingDept(response.data);
        fetchDeptTreeOptions();
        setIsModalVisible(true);
      } else {
        message.error('获取部门详情失败');
      }
    } catch (error) {
      console.error('获取部门详情失败:', error);
      message.error('获取部门详情失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (dept: DeptEntity) => {
    if (!dept.id) return;
    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除部门「${dept.name}」？如有下属子部门将无法直接删除。`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteDeptApi(dept.id!);
          if (response.restCode === '200' || response.restCode === '0') {
            message.success('删除成功');
            fetchDeptList(searchForm);
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

  const isAllExpanded = () => {
    const allKeys = getAllRowKeys(deptList);
    return (
      allKeys.length > 0 &&
      allKeys.length === expandedRowKeys.length &&
      allKeys.every((key) => expandedRowKeys.includes(key))
    );
  };

  const handleToggleExpand = () => {
    if (isAllExpanded()) {
      setExpandedRowKeys([]);
    } else {
      const allKeys = getAllRowKeys(deptList);
      setExpandedRowKeys(allKeys);
    }
  };

  const columns: ColumnsType<DeptEntity> = [
    {
      title: '部门架构名称',
      dataIndex: 'name',
      key: 'name',
      width: 260,
      render: (name: string) => (
        <Space size={8}>
          <ApartmentOutlined style={{ color: '#1677ff', fontSize: 15 }} />
          <span style={{ fontWeight: 600, color: 'var(--ap-text)' }}>{name}</span>
        </Space>
      ),
    },
    {
      title: '部门编码',
      dataIndex: 'no',
      key: 'no',
      width: 140,
      render: (no: string) => (
        <Tag color="geekblue" style={{ fontFamily: 'monospace' }}>
          {no || '-'}
        </Tag>
      ),
    },
    {
      title: '排序权重',
      dataIndex: 'orderNum',
      key: 'orderNum',
      width: 100,
      align: 'center',
      render: (num: number) => <Tag>{num ?? 0}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      align: 'center',
      render: (status: string) =>
        status === DeptStatus.ACTIVE || status === '0' ? (
          <Badge status="success" text="正常启用" />
        ) : (
          <Badge status="error" text="已停用" />
        ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 180,
      render: (date: string) => date || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record: DeptEntity) => (
        <Space size={4}>
          <Button
            size="small"
            type="link"
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record)}
          >
            新增下级
          </Button>
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
  ];

  useEffect(() => {
    fetchDeptList();
  }, []);

  return (
    <div className="crud-container fade-in">
      {/* 顶部查询卡片 */}
      <div className="crud-query-card">
        <div className="crud-filter-tabs">
          <Segmented
            value={statusTab}
            onChange={handleStatusTabChange}
            options={[
              { label: '全部部门', value: 'ALL' },
              { label: '正常启用', value: DeptStatus.ACTIVE },
              { label: '已停用', value: DeptStatus.INACTIVE },
            ]}
          />
        </div>

        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={12} md={8}>
              <Form.Item name="name" label="部门名称">
                <Input
                  placeholder="请输入部门名称"
                  prefix={<SearchOutlined style={{ color: 'var(--ap-text-3)' }} />}
                  allowClear
                />
              </Form.Item>
            </Col>
            <Col
              xs={24}
              sm={12}
              md={8}
              style={{ display: 'flex', alignItems: 'flex-end', paddingBottom: 16 }}
            >
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

      {/* 组织架构表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增部门
            </Button>
            <Button
              icon={isAllExpanded() ? <CompressOutlined /> : <ExpandOutlined />}
              onClick={handleToggleExpand}
            >
              {isAllExpanded() ? '收起全部' : '展开全部'}
            </Button>
          </div>

          <div className="crud-toolbar-right">
            <Tooltip title="刷新数据">
              <Button
                type="text"
                className="crud-tool-btn"
                icon={<ReloadOutlined spin={loading} />}
                onClick={() => fetchDeptList(searchForm)}
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
                <Button
                  type="text"
                  className="crud-tool-btn"
                  icon={<ColumnHeightOutlined />}
                />
              </Tooltip>
            </Dropdown>

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

        <Table
          columns={columns}
          dataSource={deptList}
          loading={loading}
          rowKey={(record) => record.id || `dept-${record.name}`}
          pagination={false}
          expandedRowKeys={expandedRowKeys}
          onExpandedRowsChange={(keys) => setExpandedRowKeys(keys as React.Key[])}
          size={tableSize}
          locale={{
            emptyText: deptList.length === 0 && !loading ? '暂无部门数据' : undefined,
          }}
        />
      </div>

      <Modal
        title={editingDept && editingDept.id ? '修改部门' : '添加部门'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingDept(null);
        }}
        footer={null}
        width={600}
        destroyOnHidden
      >
        <DeptForm
          key={
            editingDept && editingDept.id
              ? `edit-${editingDept.id}`
              : `new-${formKey}`
          }
          dept={editingDept}
          deptTreeOptions={deptTreeOptions}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingDept(null);
            fetchDeptList(searchForm);
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingDept(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default DeptList;
