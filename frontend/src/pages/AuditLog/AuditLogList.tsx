import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Card,
  message,
  Row,
  Col,
  Pagination,
  Breadcrumb,
  Divider,
  DatePicker,
  Modal
} from 'antd';
import {
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  EyeOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs, { type Dayjs } from 'dayjs';
import { useNavigate } from 'react-router-dom';
import {
  getAuditLogListApi
} from '../../api/audit';
import type {
  AuditLogEntity,
  AuditLogSearchForm
} from '../../types';

const { RangePicker } = DatePicker;

const AuditLogList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [auditLogList, setAuditLogList] = useState<AuditLogEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<AuditLogSearchForm>({});
  const [dateRange, setDateRange] = useState<[Dayjs | null, Dayjs | null] | null>(null);
  const [viewModalVisible, setViewModalVisible] = useState(false);
  const [viewData, setViewData] = useState<AuditLogEntity | null>(null);

  const fetchAuditLogList = async (params: AuditLogSearchForm = {}) => {
    setLoading(true);
    
    try {
      const requestParams = {
        ...params,
        pageNo: params.pageNo ?? currentPage,
        pageSize: params.pageSize ?? pageSize
      };

      const response = await getAuditLogListApi(requestParams);
      
      const responseData = response as any;
      const list = responseData?.data?.records || responseData?.records || [];
      const total = responseData?.data?.totalCount || responseData?.totalCount || 0;
      
      if (Array.isArray(list)) {
        setAuditLogList(list);
        setTotal(total);
      } else {
        setAuditLogList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取审计日志列表失败:', error);
      message.error('获取审计日志列表失败');
      setAuditLogList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: any) => {
    const params: AuditLogSearchForm = {
      category: values.category,
      module: values.module,
      user: values.user,
      event: values.event,
      status: values.status,
      startDate: dateRange?.[0] ? dayjs(dateRange[0]).format('YYYY-MM-DD HH:mm:ss') : undefined,
      endDate: dateRange?.[1] ? dayjs(dateRange[1]).format('YYYY-MM-DD HH:mm:ss') : undefined
    };
    setSearchForm(params);
    setCurrentPage(1);
    fetchAuditLogList({ ...params, pageNo: 1 });
  };

  const handleReset = () => {
    form.resetFields();
    setDateRange(null);
    setSearchForm({});
    setCurrentPage(1);
    fetchAuditLogList({ pageNo: 1 });
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchAuditLogList({ ...searchForm, pageNo: page, pageSize: size || pageSize });
  };

  const handleView = (log: AuditLogEntity) => {
    setViewData(log);
    setViewModalVisible(true);
  };

  const formatDateTime = (dateTime?: string) => {
    if (!dateTime) return '-';
    return dateTime;
  };

  const isJsonString = (str?: string): boolean => {
    if (!str) return false;
    try {
      JSON.parse(str);
      return true;
    } catch (e) {
      return false;
    }
  };

  const parseData = (data?: string): string => {
    if (!data) return '-';
    if (isJsonString(data)) {
      try {
        const obj = JSON.parse(data);
        return JSON.stringify(obj, null, 4);
      } catch {
        return data;
      }
    } else {
      return data;
    }
  };

  const columns: ColumnsType<AuditLogEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '类别',
      dataIndex: 'category',
      key: 'category',
      width: 100,
      ellipsis: true
    },
    {
      title: '模块名称',
      dataIndex: 'module',
      key: 'module',
      width: 120,
      ellipsis: true
    },
    {
      title: '操作名称',
      dataIndex: 'event',
      key: 'event',
      width: 120,
      ellipsis: true
    },
    {
      title: 'IP地址',
      dataIndex: 'ipAddress',
      key: 'ipAddress',
      width: 130,
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      ellipsis: true
    },
    {
      title: '生成日期',
      dataIndex: 'logDate',
      key: 'logDate',
      width: 160,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '用户',
      dataIndex: 'userName',
      key: 'userName',
      width: 120,
      ellipsis: true
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right',
      render: (_, record: AuditLogEntity) => (
        <Button
          size="small"
          type="primary"
          icon={<EyeOutlined />}
          onClick={() => handleView(record)}
        >
          查看
        </Button>
      )
    }
  ];

  useEffect(() => {
    fetchAuditLogList({ pageNo: 1, pageSize: 10 });
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
              title: '审计日志管理'
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
                <Form.Item name="category" label="分类名称">
                  <Input placeholder="分类名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="module" label="模块名称">
                  <Input placeholder="模块名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="user" label="操作人员">
                  <Input placeholder="操作人员" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="event" label="操作名称">
                  <Input placeholder="操作名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Input placeholder="状态" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={12}>
                <Form.Item label="创建日期">
                  <RangePicker
                    showTime
                    format="YYYY-MM-DD HH:mm:ss"
                    value={dateRange}
                    onChange={(dates) => setDateRange(dates as [Dayjs | null, Dayjs | null] | null)}
                    style={{ width: '100%' }}
                  />
                </Form.Item>
              </Col>
            </Row>
            <Row>
              <Col span={24} style={{ textAlign: 'left', marginTop: 16 }}>
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

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={auditLogList}
            loading={loading}
            rowKey={(record) => record.id || `log-${record.logDate}-${record.userName}`}
            pagination={false}
            size="small"
            scroll={{ x: 1200 }}
            locale={{
              emptyText: auditLogList.length === 0 && !loading ? '暂无数据' : undefined
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
        title="查看日志"
        open={viewModalVisible}
        onCancel={() => setViewModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setViewModalVisible(false)}>
            关闭
          </Button>
        ]}
        width={800}
        style={{ top: 20 }}
        bodyStyle={{ maxHeight: '70vh', overflow: 'auto' }}
      >
        {viewData && (
          <div>
            <div style={{ marginBottom: 16 }}>
              <strong>分类名称:</strong> {viewData.category || '-'}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>模块名称:</strong> {viewData.module || '-'}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>操作名称:</strong> {viewData.event || '-'}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>结果:</strong> {viewData.status || '-'}
            </div>
            <div style={{ marginBottom: 16 }}>
              <strong>创建日期:</strong> {formatDateTime(viewData.logDate)}
            </div>
            {viewData.beforeData && (
              <div style={{ marginBottom: 16 }}>
                <strong>操作前数据:</strong>
                <pre style={{
                  marginTop: 8,
                  padding: '8px',
                  background: '#f5f5f5',
                  borderRadius: '4px',
                  maxHeight: '200px',
                  overflow: 'auto',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word'
                }}>
                  {parseData(viewData.beforeData)}
                </pre>
              </div>
            )}
            {viewData.afterData && (
              <div style={{ marginBottom: 16 }}>
                <strong>{viewData.beforeData ? '操作后数据:' : '操作数据:'}</strong>
                <pre style={{
                  marginTop: 8,
                  padding: '8px',
                  background: '#f5f5f5',
                  borderRadius: '4px',
                  maxHeight: '200px',
                  overflow: 'auto',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word'
                }}>
                  {parseData(viewData.afterData)}
                </pre>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default AuditLogList;

