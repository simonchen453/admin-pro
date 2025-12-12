import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Card,
  message,
  Modal,
  Row,
  Col,
  Pagination,
  Divider,
  Typography
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getConfigListApi,
  deleteConfigApi
} from '../../api/config';
import type {
  ConfigEntity,
  ConfigSearchForm
} from '../../types';
import ConfigForm from './ConfigForm';

const { Title } = Typography;

const ConfigList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [configList, setConfigList] = useState<ConfigEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<ConfigSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedConfigs, setSelectedConfigs] = useState<ConfigEntity[]>([]);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingConfig, setEditingConfig] = useState<ConfigEntity | null>(null);
  const [formKey, setFormKey] = useState(0);

  const fetchConfigList = async (searchParams?: ConfigSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response = await getConfigListApi(params);

      if (response.restCode === '200' || response.restCode === '0') {
        setConfigList((response.data.records || []).map((item: any, index: number) => ({ ...item, index })));
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取配置列表失败');
        setConfigList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取配置列表失败:', error);
      message.error('数据加载失败');
      setConfigList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: ConfigSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchConfigList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchConfigList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchConfigList(searchForm, page, size);
  };

  const handleEdit = (config: ConfigEntity) => {
    setEditingConfig(config);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingConfig(null);
    setFormKey(prev => prev + 1);
    setIsModalVisible(true);
  };

  const handleBatchDelete = () => {
    if (selectedConfigs.length === 0) {
      message.warning('请选择要删除的配置');
      return;
    }

    let ids = '';
    for (let i = 0; i < selectedConfigs.length; i++) {
      ids += selectedConfigs[i].id + ',';
    }
    if (ids.indexOf(',') !== -1) {
      ids = ids.slice(0, ids.length - 1);
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedConfigs.length} 个配置吗？`,
      onOk: async () => {
        try {
          const response = await deleteConfigApi(ids);
          if (response.restCode === '200' || response.restCode === '0') {
            setSelectedConfigs([]);
            setSelectedRowKeys([]);
            fetchConfigList(searchForm, currentPage);
            message.success('配置删除成功');
          } else {
            message.error(response.message || '配置删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('配置删除失败');
        }
      }
    });
  };

  const handleDelete = (config: ConfigEntity) => {
    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除配置(${config.name})？`,
      onOk: async () => {
        try {
          const response = await deleteConfigApi(config.id!);
          if (response.restCode === '200' || response.restCode === '0') {
            fetchConfigList(searchForm, currentPage);
            message.success('配置删除成功');
          } else {
            message.error(response.message || '配置删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('配置删除失败');
        }
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: ConfigEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedConfigs(selectedRows);
    },
    getCheckboxProps: (record: ConfigEntity) => ({
      name: record.name,
    }),
  };

  const columns: ColumnsType<ConfigEntity> = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 60,
      render: (value: number) => (currentPage - 1) * pageSize + value + 1
    },
    {
      title: '配置名称',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true,
    },
    {
      title: '配置Key',
      dataIndex: 'key',
      key: 'key',
      ellipsis: true,
    },
    {
      title: '配置值',
      dataIndex: 'value',
      key: 'value',
      ellipsis: true,
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
      width: 180,
      render: (_, record: ConfigEntity) => (
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
      )
    }
  ];

  useEffect(() => {
    fetchConfigList();
  }, []);

  return (
    <div className="fade-in" style={{ padding: '0' }}>


      <Card className="modern-card" styles={{ body: { padding: '24px' } }}>
        <Form autoComplete="off"
          form={form}
          layout="inline"
          onFinish={handleSearch}
          style={{ marginBottom: 24 }}
        >
          <Row gutter={[16, 16]} style={{ width: '100%' }}>
            <Col xs={24} sm={12} md={6}>
              <Form.Item name="name" style={{ marginBottom: 0 }}>
                <Input placeholder="配置名称" allowClear prefix={<SearchOutlined style={{ color: '#cbd5e1' }} />} onPressEnter={() => form.submit()} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Form.Item name="key" style={{ marginBottom: 0 }}>
                <Input placeholder="配置键名" allowClear prefix={<SearchOutlined style={{ color: '#cbd5e1' }} />} onPressEnter={() => form.submit()} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={6}>
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
              新增配置
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              disabled={selectedConfigs.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除
            </Button>
          </Space>
        </div>

        <div className="modern-table">
          <Table
            columns={columns}
            dataSource={configList}
            loading={loading}
            rowKey={(record, index) => record.id || `row-${index}`}
            rowSelection={rowSelection}
            pagination={false}
            size="middle"
            locale={{
              emptyText: configList.length === 0 && !loading ? '暂无数据' : undefined
            }}
          />
        </div>

        <div style={{ marginTop: 24, textAlign: 'right' }}>
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
        title={editingConfig ? '修改配置' : '添加配置'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingConfig(null);
        }}
        footer={null}
        width={500}
      >
        <ConfigForm
          key={editingConfig ? `edit-${editingConfig.id}` : `new-${formKey}`}
          config={editingConfig}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingConfig(null);
            fetchConfigList(searchForm, currentPage);
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingConfig(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default ConfigList;
