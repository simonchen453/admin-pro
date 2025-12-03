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
  Breadcrumb
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
        setDomainList((response.data.records || []).map((item: any, index: number) => ({ ...item, index })));
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
      dataIndex: 'index',
      key: 'index',
      width: 60,
      render: (value: number) => (currentPage - 1) * pageSize + value + 1
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
      width: 120,
      render: (_, record: DomainEntity) => (
        <Space size="small">
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            type="primary"
            ghost
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
    <div className="fade-in" style={{ padding: '24px', minHeight: '100vh' }}>
      <div className="page-header">
        <Breadcrumb
          className="page-header-breadcrumb"
          items={[
            {
              title: (
                <Space onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
                  <HomeOutlined />
                  <span>首页</span>
                </Space>
              )
            },
            {
              title: '用户域'
            }
          ]}
        />
      </div>

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
                <Input placeholder="名称" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Form.Item name="display" style={{ marginBottom: 0 }}>
                <Input placeholder="显示名称" allowClear />
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

        <div style={{ marginBottom: 16 }}>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新增
          </Button>
        </div>

        <div className="modern-table">
          <Table
            columns={columns}
            dataSource={domainList}
            loading={loading}
            rowKey={(record) => record.id || `domain-${record.name}`}
            pagination={false}
            size="middle"
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
        title={editingDomain ? '修改用户域' : '添加用户域'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingDomain(null);
        }}
        footer={null}
        width={500}
        destroyOnHidden
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
