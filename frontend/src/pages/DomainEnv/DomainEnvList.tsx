import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Card,
  message,
  Modal,
  Row,
  Col,
  Pagination,
  Breadcrumb,
  Select
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
  getDomainEnvListApi,
  deleteDomainEnvApi
} from '../../api/domainEnv';
import { getDomainListApi } from '../../api/user';
import type {
  DomainEnvEntity,
  DomainEnvSearchForm,
  DomainEnvListResponse
} from '../../types';
import DomainEnvForm from './DomainEnvForm';

const DomainEnvList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [domainEnvList, setDomainEnvList] = useState<DomainEnvEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<DomainEnvSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [domainList, setDomainList] = useState<Array<{ id: string; name: string; display: string }>>([]);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingDomainEnv, setEditingDomainEnv] = useState<DomainEnvEntity | null>(null);

  useEffect(() => {
    fetchDomainList();
    fetchDomainEnvList();
  }, []);

  const fetchDomainList = async () => {
    try {
      const domains = await getDomainListApi();
      setDomainList(domains || []);
    } catch (error) {
      console.error('获取用户域列表失败:', error);
      setDomainList([]);
    }
  };

  const fetchDomainEnvList = async (searchParams?: DomainEnvSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response: DomainEnvListResponse = await getDomainEnvListApi(params);

      if (response.restCode === '200') {
        setDomainEnvList(response.data.records || []);
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取用户域环境配置列表失败');
        setDomainEnvList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取用户域环境配置列表失败:', error);
      message.error('获取用户域环境配置列表失败');
      setDomainEnvList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: DomainEnvSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchDomainEnvList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchDomainEnvList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    const newPageSize = size ?? pageSize;
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchDomainEnvList(undefined, page, newPageSize);
  };

  const handleEdit = (domainEnv: DomainEnvEntity) => {
    setEditingDomainEnv(domainEnv);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingDomainEnv(null);
    setIsModalVisible(true);
  };

  const handleDelete = async (domainEnv: DomainEnvEntity) => {
    Modal.confirm({
      title: '确认删除',
      content: '是否确认删除选择的数据项?',
      onOk: async () => {
        try {
          if (domainEnv.id) {
            await deleteDomainEnvApi(domainEnv.id);
            message.success('删除成功');
            fetchDomainEnvList();
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('删除失败');
        }
      }
    });
  };

  const handleDeleteMany = () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要操作的数据！');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: '是否确认删除选择的数据项?',
      onOk: async () => {
        try {
          const ids = selectedRowKeys.map(key => key.toString()).join(',');
          await deleteDomainEnvApi(ids);
          message.success('删除成功');
          setSelectedRowKeys([]);
          fetchDomainEnvList();
        } catch (error) {
          console.error('删除失败:', error);
          message.error('删除失败');
        }
      }
    });
  };

  const getDomainDisplay = (userDomain: string) => {
    const domain = domainList.find(d => d.name === userDomain);
    return domain ? domain.display : userDomain;
  };

  const columns: ColumnsType<DomainEnvEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '用户域',
      dataIndex: 'userDomain',
      key: 'userDomain',
      ellipsis: true,
      render: (text: string) => getDomainDisplay(text)
    },
    {
      title: '用户域公共角色',
      dataIndex: 'commonRole',
      key: 'commonRole',
      ellipsis: true
    },
    {
      title: '首页地址',
      dataIndex: 'homePageUrl',
      key: 'homePageUrl',
      ellipsis: true
    },
    {
      title: '登录地址',
      dataIndex: 'loginUrl',
      key: 'loginUrl',
      ellipsis: true
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_, record: DomainEnvEntity) => (
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
              title: '用户域配置'
            }
          ]}
        />
      </div>

      <Card className="modern-card" bodyStyle={{ padding: '24px' }}>
        <Form autoComplete="off"
          form={form}
          layout="inline"
          onFinish={handleSearch}
          style={{ marginBottom: 24 }}
        >
          <Row gutter={[16, 16]} style={{ width: '100%' }}>
            <Col xs={24} sm={12} md={6}>
              <Form.Item name="userDomain" style={{ marginBottom: 0 }}>
                <Select
                  placeholder="用户域"
                  allowClear
                  style={{ width: '100%' }}
                >
                  {domainList.map(domain => (
                    <Select.Option key={domain.name} value={domain.name}>
                      {domain.display}
                    </Select.Option>
                  ))}
                </Select>
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
              新增
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              onClick={handleDeleteMany}
              disabled={selectedRowKeys.length === 0}
            >
              删除
            </Button>
          </Space>
        </div>

        <div className="modern-table">
          <Table
            columns={columns}
            dataSource={domainEnvList}
            loading={loading}
            rowKey={(record) => record.id || `domainEnv-${record.userDomain}`}
            pagination={false}
            size="middle"
            rowSelection={{
              selectedRowKeys,
              onChange: (keys) => setSelectedRowKeys(keys)
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
        title={editingDomainEnv ? '修改用户域环境配置' : '添加用户域环境配置'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingDomainEnv(null);
        }}
        footer={null}
        width={500}
        destroyOnClose
      >
        <DomainEnvForm
          domainEnv={editingDomainEnv}
          domainList={domainList}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingDomainEnv(null);
            fetchDomainEnvList();
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingDomainEnv(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default DomainEnvList;
