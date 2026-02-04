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
  Pagination
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  SearchOutlined,
  ClearOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getDomainListApi,
  deleteDomainApi
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

  const handleDelete = (domain: DomainEntity) => {
    if (!domain.id) return;
    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除名称为"${domain.name}"的用户域?`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteDomainApi(domain.id!);
          if (response.restCode === '200') {
            message.success('删除成功');
            fetchDomainList();
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

  const columns: ColumnsType<DomainEntity> = [
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
      width: 180,
      render: (_, record: DomainEntity) => (
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
    fetchDomainList();
  }, []);

  return (
    <div className="fade-in" style={{ padding: '0' }}>


      <Card className="modern-card" title="用户域管理" bordered={false}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSearch}
          className="search-form"
        >
          <Row gutter={24}>
            <Col xs={24} sm={8} md={6}>
              <Form.Item name="name" label="名称">
                <Input placeholder="请输入名称" allowClear prefix={<SearchOutlined style={{ color: '#d9d9d9' }} />} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8} md={6}>
              <Form.Item name="display" label="显示名称">
                <Input placeholder="请输入显示名称" allowClear prefix={<SearchOutlined style={{ color: '#d9d9d9' }} />} />
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
