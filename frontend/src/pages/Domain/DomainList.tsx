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
  Breadcrumb,
  Divider
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getDomainListApi
} from '../../api/domain';
import type {
  DomainEntity,
  DomainSearchForm,
  DomainListResponse
} from '../../types';
import DomainForm from './DomainForm';

const DomainList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [domainList, setDomainList] = useState<DomainEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<DomainSearchForm>({});
  
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingDomain, setEditingDomain] = useState<DomainEntity | null>(null);

  const fetchDomainList = async (searchParams?: DomainSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response: DomainListResponse = await getDomainListApi(params);

      if (response.restCode === '200') {
        setDomainList(response.data.records || []);
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取用户域列表失败');
        setDomainList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取用户域列表失败:', error);
      message.error('获取用户域列表失败');
      setDomainList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: DomainSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchDomainList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchDomainList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    const newPageSize = size ?? pageSize;
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchDomainList(undefined, page, newPageSize);
  };

  const handleEdit = (domain: DomainEntity) => {
    setEditingDomain(domain);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingDomain(null);
    setIsModalVisible(true);
  };

  const columns: ColumnsType<DomainEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true
    },
    {
      title: '显示名称',
      dataIndex: 'display',
      key: 'display',
      ellipsis: true
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record: DomainEntity) => (
        <Space size="small">
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            type="primary"
          >
            修改
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    fetchDomainList();
  }, []);

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
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
            title: '用户域管理'
          }
        ]}
      />
      
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
                <Form.Item name="name" label="名称">
                  <Input placeholder="请输入名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="display" label="显示名称">
                  <Input placeholder="请输入显示名称" allowClear />
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
          </Space>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={domainList}
            loading={loading}
            rowKey={(record) => record.id || `domain-${record.name}`}
            pagination={false}
            scroll={{ x: 800 }}
            size="small"
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
        title={editingDomain ? '修改用户域' : '添加用户域'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingDomain(null);
        }}
        footer={null}
        width={500}
        destroyOnClose
      >
        <DomainForm
          domain={editingDomain}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingDomain(null);
            fetchDomainList();
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingDomain(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default DomainList;

