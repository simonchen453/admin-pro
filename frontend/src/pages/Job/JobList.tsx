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
  Divider,
  Descriptions
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  PauseCircleOutlined,
  PlayCircleOutlined,
  ThunderboltOutlined,
  EyeOutlined,
  FileTextOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getJobListApi,
  deleteJobApi,
  pauseJobApi,
  runJobApi,
  resumeJobApi,
  getNextTimeApi
} from '../../api/job';
import type {
  JobEntity,
  JobSearchForm
} from '../../types';
import { JobStatus } from '../../types';
import JobForm from './JobForm';

const JobList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [jobList, setJobList] = useState<JobEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<JobSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedJobs, setSelectedJobs] = useState<JobEntity[]>([]);
  
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingJob, setEditingJob] = useState<JobEntity | null>(null);
  const [formKey, setFormKey] = useState(0);
  const [viewModalVisible, setViewModalVisible] = useState(false);
  const [viewJob, setViewJob] = useState<JobEntity | null>(null);

  const fetchJobList = async (params: JobSearchForm = {}) => {
    setLoading(true);
    
    try {
      const response = await getJobListApi({
        ...params,
        pageNo: params.pageNo ?? currentPage,
        pageSize: params.pageSize ?? pageSize
      });
      
      const responseData = response as any;
      const list = responseData?.data?.records || responseData?.records || [];
      const total = responseData?.data?.totalCount || responseData?.totalCount || 0;
      
      if (Array.isArray(list)) {
        setJobList(list);
        setTotal(total);
      } else {
        setJobList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取定时任务列表失败:', error);
      message.error('获取定时任务列表失败');
      setJobList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: JobSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchJobList({ ...values, pageNo: 1 });
  };

  const handleReset = () => {
    form.resetFields();
    setSearchForm({});
    setCurrentPage(1);
    fetchJobList({ pageNo: 1 });
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchJobList({ ...searchForm, pageNo: page, pageSize: size || pageSize });
  };

  const handleEdit = (job: JobEntity) => {
    setEditingJob(job);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingJob(null);
    setFormKey(prev => prev + 1);
    setIsModalVisible(true);
  };

  const handleBatchDelete = () => {
    if (selectedJobs.length === 0) {
      message.warning('请选择要删除的任务');
      return;
    }
    
    let ids = '';
    for (let i = 0; i < selectedJobs.length; i++) {
      ids += selectedJobs[i].id + ',';
    }
    if (ids.indexOf(',') !== -1) {
      ids = ids.slice(0, ids.length - 1);
    }
    
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedJobs.length} 个任务吗？`,
      onOk: async () => {
        try {
          const response = await deleteJobApi(ids);
          if (response.restCode === '200' || response.restCode === 200) {
            setSelectedJobs([]);
            setSelectedRowKeys([]);
            fetchJobList(searchForm);
            message.success('定时任务删除成功');
          } else {
            message.error('定时任务删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('定时任务删除失败');
        }
      }
    });
  };

  const handleDelete = (job: JobEntity) => {
    Modal.confirm({
      title: '确认删除',
      content: `是否删除任务(${job.beanName})？`,
      onOk: async () => {
        try {
          const response = await deleteJobApi(job.id!);
          if (response.restCode === '200' || response.restCode === 200) {
            fetchJobList(searchForm);
            message.success('定时任务删除成功');
          } else {
            message.error('定时任务删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('定时任务删除失败');
        }
      }
    });
  };

  const handlePause = (job: JobEntity) => {
    Modal.confirm({
      title: '确认暂停',
      content: `是否暂停定时任务(${job.beanName})？`,
      onOk: async () => {
        try {
          const response = await pauseJobApi(job.id!);
          if (response.restCode === '200' || response.restCode === 200) {
            fetchJobList(searchForm);
            message.success('定时任务暂停成功');
          } else {
            message.error('暂停失败');
          }
        } catch (error) {
          console.error('暂停失败:', error);
          message.error('暂停失败');
        }
      }
    });
  };

  const handleResume = (job: JobEntity) => {
    Modal.confirm({
      title: '确认重启',
      content: `是否重启定时任务(${job.beanName})？`,
      onOk: async () => {
        try {
          const response = await resumeJobApi(job.id!);
          if (response.restCode === '200' || response.restCode === 200) {
            fetchJobList(searchForm);
            message.success('定时任务重启成功');
          } else {
            message.error('重启失败');
          }
        } catch (error) {
          console.error('重启失败:', error);
          message.error('重启失败');
        }
      }
    });
  };

  const handleRun = (job: JobEntity) => {
    Modal.confirm({
      title: '确认执行',
      content: `是否立即执行定时任务(${job.beanName})？`,
      onOk: async () => {
        try {
          const response = await runJobApi(job.id!);
          if (response.restCode === '200' || response.restCode === 200) {
            fetchJobList(searchForm);
            message.success('定时任务执行成功');
          } else {
            message.error('执行失败');
          }
        } catch (error) {
          console.error('执行失败:', error);
          message.error('执行失败');
        }
      }
    });
  };

  const handleView = async (job: JobEntity) => {
    setViewJob(job);
    try {
      const response = await getNextTimeApi(job.cronExpression);
      if (response.restCode === '200' || response.restCode === 200) {
        setViewJob({ ...job, nextValidTime: response.data });
      }
    } catch (error) {
      console.error('获取下次执行时间失败:', error);
    }
    setViewModalVisible(true);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: JobEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedJobs(selectedRows);
    },
    getCheckboxProps: (record: JobEntity) => ({
      name: record.beanName,
    }),
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      [JobStatus.NORMAL]: { color: 'success', text: '正常' },
      [JobStatus.PAUSE]: { color: 'default', text: '暂停' },
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  const columns: ColumnsType<JobEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '任务ID',
      dataIndex: 'id',
      key: 'id',
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
      title: 'Cron表达式',
      dataIndex: 'cronExpression',
      key: 'cronExpression',
      ellipsis: true,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      ellipsis: true,
      render: (text: string) => text || '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 300,
      render: (_, record: JobEntity) => (
        <Space size="small">
          {record.status === JobStatus.NORMAL && (
            <Button
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
              type="primary"
            >
              修改
            </Button>
          )}
          {record.status === JobStatus.PAUSE && (
            <Button
              size="small"
              icon={<PauseCircleOutlined />}
              onClick={() => handleResume(record)}
              type="primary"
            >
              启动
            </Button>
          )}
          {record.status === JobStatus.NORMAL && (
            <Button
              size="small"
              icon={<PlayCircleOutlined />}
              onClick={() => handlePause(record)}
              type="primary"
            >
              暂停
            </Button>
          )}
          {record.status === JobStatus.PAUSE && (
            <Button
              size="small"
              icon={<ThunderboltOutlined />}
              onClick={() => handleRun(record)}
              type="primary"
            >
              立即执行
            </Button>
          )}
          <Button
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
            type="primary"
          >
            查看
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    fetchJobList({ pageNo: 1, pageSize: 10 });
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
              title: '定时任务'
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
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增
            </Button>
            <Button 
              type="primary" 
              icon={<FileTextOutlined />}
              onClick={() => navigate('/admin/job/log')}
            >
              日志
            </Button>
            <Button 
              type="primary" 
              danger 
              icon={<DeleteOutlined />} 
              disabled={selectedJobs.length === 0}
              onClick={handleBatchDelete}
            >
              删除
            </Button>
          </Space>
          <div>
            已选择 {selectedJobs.length} 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={jobList}
            loading={loading}
            rowKey={(record) => record.id || `job-${record.beanName}-${record.methodName}`}
            rowSelection={rowSelection}
            pagination={false}
            size="small"
            locale={{
              emptyText: jobList.length === 0 && !loading ? '暂无数据' : undefined
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
        title={editingJob ? '编辑定时任务' : '创建定时任务'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingJob(null);
        }}
        footer={null}
        width={600}
      >
        <JobForm
          key={editingJob ? `edit-${editingJob.id}` : `new-${formKey}`}
          job={editingJob}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingJob(null);
            fetchJobList(searchForm);
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingJob(null);
          }}
        />
      </Modal>

      <Modal
        title="查看定时任务"
        open={viewModalVisible}
        onCancel={() => {
          setViewModalVisible(false);
          setViewJob(null);
        }}
        footer={[
          <Button key="close" onClick={() => {
            setViewModalVisible(false);
            setViewJob(null);
          }}>
            关闭
          </Button>
        ]}
        width={600}
      >
        {viewJob && (
          <Descriptions column={1} labelStyle={{ width: '150px' }}>
            <Descriptions.Item label="Bean名称">{viewJob.beanName}</Descriptions.Item>
            <Descriptions.Item label="方法名称">{viewJob.methodName}</Descriptions.Item>
            <Descriptions.Item label="参数">{viewJob.params || '-'}</Descriptions.Item>
            <Descriptions.Item label="Cron表达式">{viewJob.cronExpression}</Descriptions.Item>
            <Descriptions.Item label="下次执行时间">
              {viewJob.nextValidTime || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="备注">{viewJob.remark || '-'}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default JobList;

