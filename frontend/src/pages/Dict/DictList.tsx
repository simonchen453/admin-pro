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
  getDictListApi,
  deleteDictApi,
  activeDictApi,
  inactiveDictApi
} from '../../api/dict';
import type {
  DictEntity,
  DictSearchForm
} from '../../types';
import DictForm from './DictForm';

const { Option } = Select;

const DictList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [dictList, setDictList] = useState<DictEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<DictSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedDicts, setSelectedDicts] = useState<DictEntity[]>([]);
  
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingDict, setEditingDict] = useState<DictEntity | null>(null);
  const [formKey, setFormKey] = useState(0);

  const fetchDictList = async (searchParams?: DictSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response = await getDictListApi(params);

      if (response.restCode === '200' || response.restCode === '0') {
        setDictList(response.data.records || []);
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取字典列表失败');
        setDictList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取字典列表失败:', error);
      message.error('数据加载失败');
      setDictList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: DictSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchDictList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchDictList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchDictList(searchForm, page, size);
  };

  const handleEdit = (dict: DictEntity) => {
    setEditingDict(dict);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingDict(null);
    setFormKey(prev => prev + 1);
    setIsModalVisible(true);
  };

  const handleActive = (dict: DictEntity) => {
    Modal.confirm({
      title: '确认启用',
      content: `是否启用字典(${dict.name})？`,
      onOk: async () => {
        try {
          const response = await activeDictApi(dict.id!);
          if (response.restCode === '200' || response.restCode === '0') {
            fetchDictList(searchForm, currentPage);
            message.success('字典启用成功');
          } else {
            message.error(response.message || '字典启用失败');
          }
        } catch (error) {
          console.error('启用失败:', error);
          message.error('字典启用失败');
        }
      }
    });
  };

  const handleInactive = (dict: DictEntity) => {
    Modal.confirm({
      title: '确认停用',
      content: `是否停用字典(${dict.name})？`,
      onOk: async () => {
        try {
          const response = await inactiveDictApi(dict.id!);
          if (response.restCode === '200' || response.restCode === '0') {
            fetchDictList(searchForm, currentPage);
            message.success('字典停用成功');
          } else {
            message.error(response.message || '字典停用失败');
          }
        } catch (error) {
          console.error('停用失败:', error);
          message.error('字典停用失败');
        }
      }
    });
  };

  const handleBatchDelete = () => {
    if (selectedDicts.length === 0) {
      message.warning('请选择要删除的字典');
      return;
    }
    
    let ids = '';
    for (let i = 0; i < selectedDicts.length; i++) {
      ids += selectedDicts[i].id + ',';
    }
    if (ids.indexOf(',') !== -1) {
      ids = ids.slice(0, ids.length - 1);
    }
    
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedDicts.length} 个字典吗？`,
      onOk: async () => {
        try {
          const response = await deleteDictApi(ids);
          if (response.restCode === '200' || response.restCode === '0') {
            setSelectedDicts([]);
            setSelectedRowKeys([]);
            fetchDictList(searchForm, currentPage);
            message.success('字典删除成功');
          } else {
            message.error(response.message || '字典删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('字典删除失败');
        }
      }
    });
  };

  const handleDelete = (dict: DictEntity) => {
    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除字典(${dict.name})？`,
      onOk: async () => {
        try {
          const response = await deleteDictApi(dict.id!);
          if (response.restCode === '200' || response.restCode === '0') {
            fetchDictList(searchForm, currentPage);
            message.success('字典删除成功');
          } else {
            message.error(response.message || '字典删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('字典删除失败');
        }
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: DictEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedDicts(selectedRows);
    },
    getCheckboxProps: (record: DictEntity) => ({
      name: record.name,
    }),
  };

  const columns: ColumnsType<DictEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '字典名称',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true,
    },
    {
      title: '字典键值',
      dataIndex: 'key',
      key: 'key',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => (
        <Tag color={status === 'active' ? 'green' : 'red'}>
          {status === 'active' ? '启用' : '停用'}
        </Tag>
      )
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 280,
      render: (_, record: DictEntity) => (
        <Space size="small">
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            type="primary"
          >
            修改
          </Button>
          {record.status === 'inactive' && (
            <Button
              size="small"
              type="primary"
              onClick={() => handleActive(record)}
            >
              启用
            </Button>
          )}
          {record.status === 'active' && (
            <Button
              size="small"
              type="primary"
              onClick={() => handleInactive(record)}
            >
              停用
            </Button>
          )}
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
    fetchDictList();
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
              title: '字典类型管理'
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
                <Form.Item name="name" label="字典名称">
                  <Input placeholder="请输入字典名称" onPressEnter={() => form.submit()} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="key" label="字典类型">
                  <Input placeholder="请输入字典类型" onPressEnter={() => form.submit()} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Select placeholder="请选择状态" allowClear>
                    <Option value="active">启用</Option>
                    <Option value="inactive">停用</Option>
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

        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', background: 'white', borderRadius: '8px', border: '1px solid #f0f0f0' }}>
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增
            </Button>
            <Button 
              type="primary" 
              danger 
              icon={<DeleteOutlined />} 
              disabled={selectedDicts.length === 0}
              onClick={handleBatchDelete}
            >
              删除
            </Button>
          </Space>
          <div>
            已选择 {selectedDicts.length} 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={dictList}
            loading={loading}
            rowKey={(record, index) => record.id || `row-${index}`}
            rowSelection={rowSelection}
            pagination={false}
            size="small"
            locale={{
              emptyText: dictList.length === 0 && !loading ? '暂无数据' : undefined
            }}
          />
        </div>

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

      <Modal
        title={editingDict ? '修改字典类型' : '添加字典类型'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingDict(null);
        }}
        footer={null}
        width={1000}
      >
        <DictForm
          key={editingDict ? `edit-${editingDict.id}` : `new-${formKey}`}
          dict={editingDict}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingDict(null);
            fetchDictList(searchForm, currentPage);
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingDict(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default DictList;

