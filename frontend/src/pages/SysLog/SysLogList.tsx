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
  Divider,
  DatePicker
} from 'antd';
import {
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  EyeOutlined,
  DeleteOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs, { type Dayjs } from 'dayjs';
import { useNavigate } from 'react-router-dom';
import {
  getSysLogListApi,
  deleteManySysLogApi
} from '../../api/syslog';
import type {
  SysLogEntity,
  SysLogSearchForm
} from '../../types';

const { RangePicker } = DatePicker;

const SysLogList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [sysLogList, setSysLogList] = useState<SysLogEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<SysLogSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedLogs, setSelectedLogs] = useState<SysLogEntity[]>([]);
  const [dateRange, setDateRange] = useState<[Dayjs | null, Dayjs | null] | null>(null);

  const fetchSysLogList = async (params: SysLogSearchForm = {}) => {
    setLoading(true);
    
    try {
      const requestParams = {
        ...params,
        pageNo: params.pageNo ?? currentPage,
        pageSize: params.pageSize ?? pageSize
      };

      const response = await getSysLogListApi(requestParams);
      
      const responseData = response as any;
      const list = responseData?.data?.records || responseData?.records || [];
      const total = responseData?.data?.totalCount || responseData?.totalCount || 0;
      
      if (Array.isArray(list)) {
        setSysLogList(list);
        setTotal(total);
      } else {
        setSysLogList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取系统日志列表失败:', error);
      message.error('获取系统日志列表失败');
      setSysLogList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: any) => {
    const params: SysLogSearchForm = {
      condition: values.condition,
      startTime: dateRange?.[0] ? dayjs(dateRange[0]).format('YYYY-MM-DD HH:mm:ss') : undefined,
      endTime: dateRange?.[1] ? dayjs(dateRange[1]).format('YYYY-MM-DD HH:mm:ss') : undefined
    };
    setSearchForm(params);
    setCurrentPage(1);
    fetchSysLogList({ ...params, pageNo: 1 });
  };

  const handleReset = () => {
    form.resetFields();
    setDateRange(null);
    setSearchForm({});
    setCurrentPage(1);
    fetchSysLogList({ pageNo: 1 });
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchSysLogList({ ...searchForm, pageNo: page, pageSize: size || pageSize });
  };

  const handleView = (log: SysLogEntity) => {
    navigate(`/admin/syslog/view?id=${log.id}`);
  };

  const handleBatchDelete = () => {
    if (selectedLogs.length === 0) {
      message.warning('请选择要删除的日志');
      return;
    }
    
    let ids = '';
    for (let i = 0; i < selectedLogs.length; i++) {
      ids += selectedLogs[i].id + ',';
    }
    if (ids.indexOf(',') !== -1) {
      ids = ids.slice(0, ids.length - 1);
    }
    
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedLogs.length} 条日志吗？`,
      onOk: async () => {
        try {
          const response = await deleteManySysLogApi(ids);
          if (response.restCode === '200' || response.restCode === 200) {
            setSelectedLogs([]);
            setSelectedRowKeys([]);
            fetchSysLogList(searchForm);
            message.success('系统日志删除成功');
          } else {
            message.error('系统日志删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('系统日志删除失败');
        }
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: SysLogEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedLogs(selectedRows);
    },
    getCheckboxProps: (record: SysLogEntity) => ({
      name: record.id,
    }),
  };

  const formatDateTime = (dateTime?: string) => {
    if (!dateTime) return '-';
    return dateTime;
  };

  const columns: ColumnsType<SysLogEntity> = [
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
      width: 100,
      ellipsis: true
    },
    {
      title: '用户登录名',
      dataIndex: 'loginName',
      key: 'loginName',
      width: 250,
      ellipsis: true
    },
    {
      title: '用户IP',
      dataIndex: 'ip',
      key: 'ip',
      width: 150,
      ellipsis: true
    },
    {
      title: '用户浏览器',
      dataIndex: 'browser',
      key: 'browser',
      width: 150,
      ellipsis: true
    },
    {
      title: '访问方法',
      dataIndex: 'method',
      key: 'method',
      minWidth: 280,
      ellipsis: true
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      width: 120,
      ellipsis: true
    },
    {
      title: '消耗时间(ms)',
      dataIndex: 'time',
      key: 'time',
      width: 120,
      render: (time: number) => time !== undefined && time !== null ? `${time}` : '-'
    },
    {
      title: '时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 140,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right',
      render: (_, record: SysLogEntity) => (
        <Space size="small">
          <Button
            size="small"
            type="primary"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
          >
            查看
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    fetchSysLogList({ pageNo: 1, pageSize: 10 });
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
              title: '系统日志管理'
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
                <Form.Item name="condition" label="关键字">
                  <Input placeholder="关键字" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={12}>
                <Form.Item label="时间范围">
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

        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', background: 'white', borderRadius: '8px', border: '1px solid #f0f0f0' }}>
          <Space>
            <Button 
              type="primary" 
              danger 
              icon={<DeleteOutlined />} 
              disabled={selectedLogs.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除
            </Button>
          </Space>
          <div>
            已选择 {selectedLogs.length} 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={sysLogList}
            loading={loading}
            rowKey={(record) => record.id || `log-${record.loginName}-${record.createdDate}`}
            rowSelection={rowSelection}
            pagination={false}
            size="small"
            scroll={{ x: 1500 }}
            locale={{
              emptyText: sysLogList.length === 0 && !loading ? '暂无数据' : undefined
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
    </div>
  );
};

export default SysLogList;

