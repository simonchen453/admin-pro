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
  Breadcrumb,
  Divider,
  Image,
  Row,
  Col
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  ExpandOutlined,
  CompressOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getDeptListApi,
  getDeptDetailApi,
  deleteDeptApi,
  getDeptTreeSelectApi
} from '../../api/dept';
import type {
  DeptEntity,
  DeptSearchForm
} from '../../types';
import { DeptStatus } from '../../types';
import DeptForm from './DeptForm';

const { Option } = Select;

const DeptList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [deptList, setDeptList] = useState<DeptEntity[]>([]);
  const [searchForm, setSearchForm] = useState<DeptSearchForm>({});
  
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingDept, setEditingDept] = useState<DeptEntity | null>(null);
  const [formKey, setFormKey] = useState(0);
  const [deptTreeOptions, setDeptTreeOptions] = useState<any[]>([]);
  const [viewImageVisible, setViewImageVisible] = useState(false);
  const [viewImageUrl, setViewImageUrl] = useState('');
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
      items.forEach(item => {
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
      const treeData = response.data || [];
      const options = [
        {
          id: '0',
          label: '主类目',
          children: convertTreeSelectData(treeData)
        }
      ];
      setDeptTreeOptions(options);
    } catch (error) {
      console.error('获取部门树选项失败:', error);
      setDeptTreeOptions([]);
    }
  };

  const convertTreeSelectData = (data: any[]): any[] => {
    return data.map(item => ({
      title: item.label,
      value: item.id,
      key: item.id,
      children: item.children ? convertTreeSelectData(item.children) : undefined
    }));
  };

  const handleSearch = (values: DeptSearchForm) => {
    setSearchForm(values);
    fetchDeptList(values);
  };

  const handleReset = () => {
    form.resetFields();
    setSearchForm({});
    fetchDeptList({});
  };

  const handleAdd = (parentDept?: DeptEntity) => {
    setEditingDept(null);
    setFormKey(prev => prev + 1);
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
      content: `是否确认删除名称为"${dept.name}"的数据项?`,
      okText: '确定',
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
      }
    });
  };

  const getStatusTag = (status?: string) => {
    if (status === DeptStatus.ACTIVE) {
      return <Tag color="green">正常</Tag>;
    } else if (status === DeptStatus.INACTIVE) {
      return <Tag color="red">停用</Tag>;
    }
    return <Tag>{status || '-'}</Tag>;
  };

  const formatDate = (dateStr?: string) => {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return dateStr;
    }
  };

  const handleViewImage = (url?: string) => {
    if (url) {
      setViewImageUrl(url.startsWith('http') ? url : `${import.meta.env.VITE_API_BASE || '/api'}${url}`);
      setViewImageVisible(true);
    }
  };

  const isAllExpanded = () => {
    const allKeys = getAllRowKeys(deptList);
    return allKeys.length > 0 && allKeys.length === expandedRowKeys.length && 
           allKeys.every(key => expandedRowKeys.includes(key));
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
      title: '部门名称',
      dataIndex: 'name',
      key: 'name',
      width: 200
    },
    {
      title: '部门编号',
      dataIndex: 'no',
      key: 'no',
      width: 200
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      key: 'orderNum',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 180,
      render: (date: string) => formatDate(date)
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 240,
      render: (_, record: DeptEntity) => (
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
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record)}
            type="primary"
          >
            新增
          </Button>
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
    fetchDeptList();
  }, []);

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
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
              title: '部门管理'
            }
          ]}
        />
      </div>
      
      <Divider />

      <Card>
        <Card size="small" style={{ marginBottom: 16 }}>
          <Form
            form={form}
            layout="inline"
            onFinish={handleSearch}
          >
            <Row gutter={[16, 16]} style={{ width: '100%' }}>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="name" label="部门名称">
                  <Input placeholder="请输入部门名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Select placeholder="部门状态" allowClear style={{ width: '100%' }}>
                    <Option value={DeptStatus.ACTIVE}>正常</Option>
                    <Option value={DeptStatus.INACTIVE}>停用</Option>
                  </Select>
                </Form.Item>
              </Col>
            </Row>
            <Row>
              <Col span={24} style={{ textAlign: 'left', marginTop: 16, marginLeft: 2, marginBottom: 10 }}>
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

        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', background: 'white', borderRadius: '8px', border: '1px solid #f0f0f0' }}>
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd()}>
              新增
            </Button>
            <Button 
              icon={isAllExpanded() ? <CompressOutlined /> : <ExpandOutlined />} 
              onClick={handleToggleExpand}
            >
              {isAllExpanded() ? '收起全部' : '展开全部'}
            </Button>
          </Space>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={deptList}
            loading={loading}
            rowKey="id"
            pagination={false}
            size="small"
            expandedRowKeys={expandedRowKeys}
            onExpandedRowsChange={setExpandedRowKeys}
            locale={{
              emptyText: deptList.length === 0 && !loading ? '暂无数据' : undefined
            }}
          />
        </div>
      </Card>

      <Modal
        title={editingDept?.id ? '修改部门' : '添加部门'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingDept(null);
        }}
        footer={null}
        width={800}
      >
        <DeptForm
          key={editingDept?.id ? `edit-${editingDept.id}` : `new-${formKey}`}
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

      <Modal
        title="查看图片"
        open={viewImageVisible}
        onCancel={() => setViewImageVisible(false)}
        footer={[
          <Button key="ok" type="primary" onClick={() => setViewImageVisible(false)}>
            确定
          </Button>
        ]}
        width={800}
      >
        <div style={{ textAlign: 'center' }}>
          <Image src={viewImageUrl} alt="部门图标" style={{ maxWidth: '100%' }} />
        </div>
      </Modal>
    </div>
  );
};

export default DeptList;

