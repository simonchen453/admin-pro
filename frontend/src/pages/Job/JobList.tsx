import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Tag,
  message,
  Modal,
  Row,
  Col,
  Descriptions,
  Segmented,
  Tooltip,
  Dropdown,
  Popover,
  Checkbox,
  Badge,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  PauseCircleOutlined,
  PlayCircleOutlined,
  ThunderboltOutlined,
  EyeOutlined,
  FileTextOutlined,
  ReloadOutlined,
  ColumnHeightOutlined,
  SettingOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  InfoCircleFilled,
  ClockCircleOutlined,
  DownOutlined,
  CodeOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getJobListApi,
  deleteJobApi,
  pauseJobApi,
  runJobApi,
  resumeJobApi,
  getNextTimeApi,
} from '../../api/job';
import type {
  JobEntity,
  JobSearchForm,
} from '../../types';
import { JobStatus } from '../../types';
import JobForm from './JobForm';
import '../../styles/crud.css';

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

  // ProTable 状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [statusTab, setStatusTab] = useState<string>('ALL');
  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'taskInfo',
    'cronExpression',
    'status',
    'remark',
    'action',
  ]);

  const tableContainerRef = useRef<HTMLDivElement>(null);

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
        pageSize: params.pageSize ?? pageSize,
      });

      const responseData = response as any;
      const list = responseData?.data?.records || responseData?.records || [];
      const totalCount = responseData?.data?.totalCount || responseData?.totalCount || 0;

      if (Array.isArray(list)) {
        setJobList(list.map((item: any, index: number) => ({ ...item, index })));
        setTotal(totalCount);
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
    const query = {
      ...values,
      status: statusTab === 'ALL' ? values.status : statusTab,
    };
    setSearchForm(query);
    setCurrentPage(1);
    fetchJobList({ ...query, pageNo: 1 });
  };

  const handleReset = () => {
    form.resetFields();
    setStatusTab('ALL');
    setSearchForm({});
    setCurrentPage(1);
    fetchJobList({ pageNo: 1 });
  };

  const handleStatusTabChange = (val: string | number) => {
    const tab = String(val);
    setStatusTab(tab);
    setCurrentPage(1);
    const newStatus = tab === 'ALL' ? undefined : tab;
    const newForm = { ...searchForm, status: newStatus };
    setSearchForm(newForm);
    fetchJobList({ ...newForm, pageNo: 1 });
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
    setFormKey((prev) => prev + 1);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingJob(null);
    setFormKey((prev) => prev + 1);
    setIsModalVisible(true);
  };

  const handleDelete = (job: JobEntity) => {
    if (!job.id) return;
    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除任务「${job.beanName}」？`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteJobApi(job.id!);
          if (response.restCode === '200') {
            message.success('删除成功');
            fetchJobList({ pageNo: currentPage });
          } else {
            message.error(response.message || '删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('删除失败');
        }
      },
    });
  };

  const handleBatchDelete = () => {
    if (selectedJobs.length === 0) {
      message.warning('请选择要删除的任务');
      return;
    }
    const ids = selectedJobs.map((job) => job.id).filter(Boolean).join(',');

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedJobs.length} 个任务吗？`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteJobApi(ids);
          if (response.restCode === '200') {
            message.success('批量删除成功');
            setSelectedRowKeys([]);
            setSelectedJobs([]);
            fetchJobList({ pageNo: currentPage });
          } else {
            message.error(response.message || '批量删除失败');
          }
        } catch (error) {
          console.error('批量删除失败:', error);
          message.error('批量删除失败');
        }
      },
    });
  };

  const handlePause = async (job: JobEntity) => {
    if (!job.id) return;
    try {
      const response = await pauseJobApi(job.id);
      if (response.restCode === '200') {
        message.success('任务已暂停');
        fetchJobList({ pageNo: currentPage });
      } else {
        message.error(response.message || '暂停失败');
      }
    } catch (error) {
      console.error('暂停失败:', error);
      message.error('暂停失败');
    }
  };

  const handleResume = async (job: JobEntity) => {
    if (!job.id) return;
    try {
      const response = await resumeJobApi(job.id);
      if (response.restCode === '200') {
        message.success('任务已恢复');
        fetchJobList({ pageNo: currentPage });
      } else {
        message.error(response.message || '恢复失败');
      }
    } catch (error) {
      console.error('恢复失败:', error);
      message.error('恢复失败');
    }
  };

  const handleRun = async (job: JobEntity) => {
    if (!job.id) return;
    try {
      const response = await runJobApi(job.id);
      if (response.restCode === '200') {
        message.success('已触发任务立即执行');
      } else {
        message.error(response.message || '执行失败');
      }
    } catch (error) {
      console.error('执行失败:', error);
      message.error('执行失败');
    }
  };

  const handleView = async (job: JobEntity) => {
    setViewJob(job);
    try {
      const response = await getNextTimeApi(job.cronExpression);
      if (response.restCode === '200') {
        setViewJob({ ...job, nextValidTime: response.data });
      }
    } catch (error) {
      console.error('获取下次执行时间失败:', error);
    }
    setViewModalVisible(true);
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[], rows: JobEntity[]) => {
      setSelectedRowKeys(keys);
      setSelectedJobs(rows);
    },
  };

  const allColumns: ColumnsType<JobEntity> = useMemo(
    () => [
      {
        title: '任务调用信息',
        key: 'taskInfo',
        width: 320,
        render: (_, record: JobEntity) => (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            <Space size={6}>
              <CodeOutlined style={{ color: '#1677ff' }} />
              <span style={{ fontWeight: 600, color: 'var(--ap-text)' }}>
                {record.beanName}
              </span>
              <Tag color="blue" style={{ fontFamily: 'monospace' }}>
                #{record.methodName}()
              </Tag>
            </Space>
            {record.params && (
              <span
                style={{
                  fontSize: 12,
                  color: 'var(--ap-text-3)',
                  fontFamily: 'monospace',
                }}
              >
                入参: {record.params}
              </span>
            )}
          </div>
        ),
      },
      {
        title: 'Cron 调度表达式',
        dataIndex: 'cronExpression',
        key: 'cronExpression',
        width: 180,
        render: (cron: string, record: JobEntity) => (
          <Tooltip title="点击查看详情与下次触发时间">
            <Tag
              color="geekblue"
              style={{ cursor: 'pointer', fontFamily: 'monospace' }}
              onClick={() => handleView(record)}
            >
              <ClockCircleOutlined style={{ marginRight: 4 }} />
              {cron}
            </Tag>
          </Tooltip>
        ),
      },
      {
        title: '任务状态',
        dataIndex: 'status',
        key: 'status',
        width: 110,
        align: 'center',
        render: (status: string) =>
          status === JobStatus.NORMAL || status === '0' ? (
            <Badge status="processing" text="运行中" />
          ) : (
            <Badge status="default" text="已暂停" />
          ),
      },
      {
        title: '任务备注',
        dataIndex: 'remark',
        key: 'remark',
        ellipsis: true,
        render: (text: string) => text || '-',
      },
      {
        title: '操作',
        key: 'action',
        fixed: 'right',
        width: 180,
        render: (_, record: JobEntity) => (
          <Space size={4}>
            <Button
              size="small"
              type="link"
              icon={<ThunderboltOutlined />}
              onClick={() => handleRun(record)}
            >
              执行一次
            </Button>
            <Button
              size="small"
              type="link"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              修改
            </Button>
            <Dropdown
              menu={{
                items: [
                  record.status === JobStatus.PAUSE
                    ? {
                        key: 'resume',
                        icon: <PlayCircleOutlined />,
                        label: '恢复任务',
                        onClick: () => handleResume(record),
                      }
                    : {
                        key: 'pause',
                        icon: <PauseCircleOutlined />,
                        label: '暂停任务',
                        danger: true,
                        onClick: () => handlePause(record),
                      },
                  {
                    key: 'detail',
                    icon: <EyeOutlined />,
                    label: '执行详情',
                    onClick: () => handleView(record),
                  },
                  { type: 'divider' },
                  {
                    key: 'delete',
                    icon: <DeleteOutlined />,
                    label: '删除任务',
                    danger: true,
                    onClick: () => handleDelete(record),
                  },
                ],
              }}
              trigger={['click']}
            >
              <Button size="small" type="link">
                更多 <DownOutlined style={{ fontSize: 10 }} />
              </Button>
            </Dropdown>
          </Space>
        ),
      },
    ],
    []
  );

  const columns = useMemo(() => {
    return allColumns.filter((col) => !col.key || visibleColumns.includes(col.key as string));
  }, [allColumns, visibleColumns]);

  const columnSettingContent = (
    <div style={{ width: 180 }}>
      <div className="crud-col-popover-title">
        <span>展示列配置</span>
        <Button
          type="link"
          size="small"
          style={{ padding: 0 }}
          onClick={() =>
            setVisibleColumns(['taskInfo', 'cronExpression', 'status', 'remark', 'action'])
          }
        >
          重置
        </Button>
      </div>
      {allColumns.map((col) => {
        if (!col.key || col.key === 'action') return null;
        const key = col.key as string;
        const checked = visibleColumns.includes(key);
        return (
          <div key={key} className="crud-col-item">
            <Checkbox
              checked={checked}
              onChange={(e) => {
                if (e.target.checked) {
                  setVisibleColumns([...visibleColumns, key]);
                } else {
                  setVisibleColumns(visibleColumns.filter((k) => k !== key));
                }
              }}
            >
              {typeof col.title === 'string' ? col.title : key}
            </Checkbox>
          </div>
        );
      })}
    </div>
  );

  useEffect(() => {
    fetchJobList({ pageNo: 1, pageSize: 10 });
  }, []);

  return (
    <div className="crud-container fade-in">
      {/* 顶部独立查询卡片 */}
      <div className="crud-query-card">
        <div className="crud-filter-tabs">
          <Segmented
            value={statusTab}
            onChange={handleStatusTabChange}
            options={[
              { label: '全部任务', value: 'ALL' },
              { label: '运行中', value: JobStatus.NORMAL },
              { label: '已暂停', value: JobStatus.PAUSE },
            ]}
          />
        </div>

        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={12} md={8}>
              <Form.Item name="condition" label="Bean / 方法 / 描述">
                <Input
                  placeholder="请输入Bean名称或关键字"
                  allowClear
                  prefix={<SearchOutlined style={{ color: 'var(--ap-text-3)' }} />}
                />
              </Form.Item>
            </Col>
            <Col
              xs={24}
              sm={12}
              md={8}
              style={{ display: 'flex', alignItems: 'flex-end', paddingBottom: 16 }}
            >
              <Space size={8}>
                <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
                  查询
                </Button>
                <Button onClick={handleReset} icon={<ClearOutlined />}>
                  重置
                </Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </div>

      {/* 任务表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增任务
            </Button>
            <Button
              icon={<FileTextOutlined />}
              onClick={() => navigate('/admin/job/log')}
            >
              调度日志
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              disabled={selectedJobs.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除
            </Button>
          </div>

          <div className="crud-toolbar-right">
            <Tooltip title="刷新数据">
              <Button
                type="text"
                className="crud-tool-btn"
                icon={<ReloadOutlined spin={loading} />}
                onClick={() => fetchJobList(searchForm)}
              />
            </Tooltip>

            <Dropdown
              menu={{
                items: [
                  { key: 'large', label: '宽松' },
                  { key: 'middle', label: '默认' },
                  { key: 'small', label: '紧凑' },
                ],
                selectedKeys: [tableSize],
                onClick: (e) => setTableSize(e.key as 'small' | 'middle' | 'large'),
              }}
              trigger={['click']}
            >
              <Tooltip title="表格密度">
                <Button
                  type="text"
                  className="crud-tool-btn"
                  icon={<ColumnHeightOutlined />}
                />
              </Tooltip>
            </Dropdown>

            <Popover content={columnSettingContent} trigger="click" placement="bottomRight">
              <Tooltip title="列显隐设置">
                <Button type="text" className="crud-tool-btn" icon={<SettingOutlined />} />
              </Tooltip>
            </Popover>

            <Tooltip title={isFullscreen ? '退出全屏' : '全屏聚焦'}>
              <Button
                type="text"
                className="crud-tool-btn"
                icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
                onClick={() => setIsFullscreen(!isFullscreen)}
              />
            </Tooltip>
          </div>
        </div>

        {selectedRowKeys.length > 0 && (
          <div className="crud-selection-bar">
            <div className="crud-selection-bar-text">
              <InfoCircleFilled />
              <span>
                已选择 <strong style={{ color: '#1677ff' }}>{selectedRowKeys.length}</strong> 个任务
              </span>
              <Button
                type="link"
                size="small"
                style={{ padding: 0 }}
                onClick={() => {
                  setSelectedRowKeys([]);
                  setSelectedJobs([]);
                }}
              >
                清空已选
              </Button>
            </div>
            <Space size={8}>
              <Button
                size="small"
                danger
                icon={<DeleteOutlined />}
                onClick={handleBatchDelete}
              >
                确认删除所选
              </Button>
            </Space>
          </div>
        )}

        <Table
          columns={columns}
          dataSource={jobList}
          loading={loading}
          rowKey={(record) => record.id || `job-${record.beanName}-${record.methodName}`}
          rowSelection={rowSelection}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (totalCount, range) =>
              `第 ${range[0]}-${range[1]} 条 / 共 ${totalCount} 条任务`,
            onChange: handlePageChange,
            onShowSizeChange: handlePageChange,
            pageSizeOptions: ['10', '20', '30', '50'],
          }}
          size={tableSize}
          locale={{
            emptyText: jobList.length === 0 && !loading ? '暂无定时任务' : undefined,
          }}
        />
      </div>

      {/* 新增/编辑任务模态框 */}
      <Modal
        title={editingJob ? '编辑任务' : '新增任务'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingJob(null);
        }}
        footer={null}
        width={700}
        destroyOnHidden
      >
        <JobForm
          key={editingJob ? `edit-${editingJob.id}` : `new-${formKey}`}
          job={editingJob}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingJob(null);
            fetchJobList({ pageNo: currentPage });
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingJob(null);
          }}
        />
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="定时任务调度详情"
        open={viewModalVisible}
        onCancel={() => {
          setViewModalVisible(false);
          setViewJob(null);
        }}
        footer={[
          <Button
            key="close"
            type="primary"
            onClick={() => {
              setViewModalVisible(false);
              setViewJob(null);
            }}
          >
            知道了
          </Button>,
        ]}
        width={650}
      >
        {viewJob && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="Bean名称">
              <strong>{viewJob.beanName}</strong>
            </Descriptions.Item>
            <Descriptions.Item label="方法名称">
              <code>{viewJob.methodName}()</code>
            </Descriptions.Item>
            <Descriptions.Item label="参数值">
              <code>{viewJob.params || '无'}</code>
            </Descriptions.Item>
            <Descriptions.Item label="Cron表达式">
              <Tag color="blue">{viewJob.cronExpression}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="下次执行时间">
              {viewJob.nextValidTime ? (
                <Tag color="green">{viewJob.nextValidTime}</Tag>
              ) : (
                '计算中或未配置'
              )}
            </Descriptions.Item>
            <Descriptions.Item label="当前状态">
              {viewJob.status === JobStatus.NORMAL ? (
                <Badge status="processing" text="正常执行中" />
              ) : (
                <Badge status="default" text="已暂停" />
              )}
            </Descriptions.Item>
            <Descriptions.Item label="备注信息">
              {viewJob.remark || '-'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default JobList;
