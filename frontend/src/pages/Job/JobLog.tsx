import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
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
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getJobLogListApi,
  deleteJobLogApi,
  deleteAllJobLogApi
} from '../../api/job';
import type {
  JobLogEntity,
  JobLogSearchForm
} from '../../types';
import { JobLogStatus } from '../../types';

const JobLog: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [logList, setLogList] = useState<JobLogEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<JobLogSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedLogs, setSelectedLogs] = useState<JobLogEntity[]>([]);

  const fetchLogList = async (params: JobLogSearchForm = {}) => {
    setLoading(true);
    
    try {
      const response = await getJobLogListApi({
        ...params,
        pageNo: params.pageNo ?? currentPage,
        pageSize: params.pageSize ?? pageSize
      });
      
      const responseData = response as any;
      const list = responseData?.data?.records || responseData?.records || [];
      const total = responseData?.data?.totalCount || responseData?.totalCount || 0;
      
      if (Array.isArray(list)) {
        setLogList(list);
        setTotal(total);
      } else {
        setLogList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取定时任务日志列表失败:', error);
      message.error('获取定时任务日志列表失败');
      setLogList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: JobLogSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchLogList({ ...values, pageNo: 1 });
  };

  const handleReset = () => {
    form.resetFields();
    setSearchForm({});
    setCurrentPage(1);
    fetchLogList({ pageNo: 1 });
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchLogList({ ...searchForm, pageNo: page, pageSize: size || pageSize });
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
          const response = await deleteJobLogApi(ids);
          if (response.restCode === '200' || response.restCode === 200) {
            setSelectedLogs([]);
            setSelectedRowKeys([]);
            fetchLogList(searchForm);
            message.success('定时任务日志删除成功');
          } else {
            message.error('定时任务日志删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('定时任务日志删除失败');
        }
      }
    });
  };

  const handleDeleteAll = () => {
    Modal.confirm({
      title: '确认清空',
      content: '是否清空所有日志？',
      onOk: async () => {
        try {
          const response = await deleteAllJobLogApi();
          if (response.restCode === '200' || response.restCode === 200) {
            setSelectedLogs([]);
            setSelectedRowKeys([]);
            fetchLogList(searchForm);
            message.success('定时任务日志清空成功');
          } else {
            message.error('定时任务日志清空失败');
          }
        } catch (error) {
          console.error('清空失败:', error);
          message.error('定时任务日志清空失败');
        }
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: JobLogEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedLogs(selectedRows);
    },
    getCheckboxProps: (record: JobLogEntity) => ({
      name: record.id,
    }),
  };

  const getStatusTag = (status: string) => {
    if (status === JobLogStatus.SUCCESS) {
      return <Tag color="success">成功</Tag>;
    } else if (status === JobLogStatus.FAIL) {
      return <Tag color="error">失败</Tag>;
    }
    return <Tag>{status}</Tag>;
  };

  const columns: ColumnsType<JobLogEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '任务ID',
      dataIndex: 'jobId',
      key: 'jobId',
      width: 200,
      ellipsis: true,
    },
    {
      title: 'Bean名称',
      dataIndex: 'beanName',
      key: 'beanName',
      ellipsis: true,
    },
    {
      title: '方法名称',
      dataIndex: 'methodName',
      key: 'methodName',
      ellipsis: true,
    },
    {
      title: '参数',
      dataIndex: 'params',
      key: 'params',
      ellipsis: true,
      render: (text: string) => text || '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '执行时长（毫秒）',
      dataIndex: 'times',
      key: 'times',
      width: 150,
      render: (times: number) => times || '-'
    },
    {
      title: '执行时间',
      dataIndex: 'createTime',
      key: 'createTime',
      ellipsis: true,
      render: (time: string) => time || '-'
    }
  ];

  useEffect(() => {
    fetchLogList({ pageNo: 1, pageSize: 10 });
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
              title: (
                <Button
                  type="link"
                  icon={<ArrowLeftOutlined />}
                  onClick={() => navigate('/admin/job')}
                  style={{ padding: 0, height: 'auto', lineHeight: 1 }}
                >
                  定时任务
                </Button>
              )
            },
            {
              title: '定时任务日志'
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
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="condition" label="Bean名称">
                  <Input placeholder="关键字" allowClear />
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
            <Button 
              type="primary" 
              icon={<ArrowLeftOutlined />}
              onClick={() => navigate('/admin/job')}
            >
              返回
            </Button>
            <Button 
              type="primary" 
              icon={<DeleteOutlined />}
              onClick={handleBatchDelete}
              disabled={selectedLogs.length === 0}
            >
              批量删除
            </Button>
            <Button 
              type="primary" 
              danger 
              icon={<DeleteOutlined />}
              onClick={handleDeleteAll}
            >
              清空日志
            </Button>
          </Space>
          <div>
            已选择 {selectedLogs.length} 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={logList}
            loading={loading}
            rowKey={(record) => record.id || `log-${record.jobId}-${record.createTime}`}
            rowSelection={rowSelection}
            pagination={false}
            size="small"
            locale={{
              emptyText: logList.length === 0 && !loading ? '暂无数据' : undefined
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

export default JobLog;

