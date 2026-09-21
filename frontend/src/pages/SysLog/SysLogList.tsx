import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  message,
  Modal,
  Row,
  Col,
  DatePicker,
  Tag,
  Tooltip,
  Dropdown,
  Popover,
  Checkbox,
  Drawer,
  Descriptions,
} from 'antd';
import {
  SearchOutlined,
  ClearOutlined,
  EyeOutlined,
  DeleteOutlined,
  ReloadOutlined,
  ColumnHeightOutlined,
  SettingOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  InfoCircleFilled,
  ClockCircleOutlined,
  UserOutlined,
  GlobalOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs, { type Dayjs } from 'dayjs';
import { useNavigate } from 'react-router-dom';
import {
  getSysLogListApi,
  deleteManySysLogApi,
} from '../../api/syslog';
import type {
  SysLogEntity,
  SysLogSearchForm,
} from '../../types';
import '../../styles/crud.css';

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

  // ProTable 状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'loginName',
    'ip',
    'clientInfo',
    'method',
    'time',
    'createdDate',
    'action',
  ]);

  // 抽屉详情预览
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [activeLog, setActiveLog] = useState<SysLogEntity | null>(null);

  const tableContainerRef = useRef<HTMLDivElement>(null);

  const fetchSysLogList = async (params: SysLogSearchForm = {}) => {
    setLoading(true);
    try {
      const requestParams = {
        ...params,
        pageNo: params.pageNo ?? currentPage,
        pageSize: params.pageSize ?? pageSize,
      };

      const response = await getSysLogListApi(requestParams);
      const responseData = response as any;
      const list = responseData?.data?.records || responseData?.records || [];
      const totalCount = responseData?.data?.totalCount || responseData?.totalCount || 0;

      if (Array.isArray(list)) {
        setSysLogList(list.map((item: any, index: number) => ({ ...item, index })));
        setTotal(totalCount);
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
      endTime: dateRange?.[1] ? dayjs(dateRange[1]).format('YYYY-MM-DD HH:mm:ss') : undefined,
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

  // 快捷时间范围
  const handleQuickDate = (days: number) => {
    const end = dayjs().endOf('day');
    const start = dayjs().subtract(days, 'day').startOf('day');
    setDateRange([start, end]);
    form.setFieldsValue({ dateRange: [start, end] });
    const params: SysLogSearchForm = {
      ...searchForm,
      startTime: start.format('YYYY-MM-DD HH:mm:ss'),
      endTime: end.format('YYYY-MM-DD HH:mm:ss'),
    };
    setSearchForm(params);
    setCurrentPage(1);
    fetchSysLogList({ ...params, pageNo: 1 });
  };

  // 查看详情
  const handleView = (log: SysLogEntity) => {
    setActiveLog(log);
    setDrawerVisible(true);
  };

  // 批量删除
  const handleBatchDelete = () => {
    if (selectedLogs.length === 0) {
      message.warning('请选择要删除的日志');
      return;
    }

    const ids = selectedLogs
      .map((log) => log.id)
      .filter(Boolean)
      .join(',');

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedLogs.length} 条审计日志吗？删除后不可恢复。`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteManySysLogApi(ids);
          if (response.restCode === '200') {
            setSelectedLogs([]);
            setSelectedRowKeys([]);
            fetchSysLogList(searchForm);
            message.success('系统日志删除成功');
          } else {
            message.error(response.message || '系统日志删除失败');
          }
        } catch (error) {
          console.error('删除失败:', error);
          message.error('系统日志删除失败');
        }
      },
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[], rows: SysLogEntity[]) => {
      setSelectedRowKeys(keys);
      setSelectedLogs(rows);
    },
    getCheckboxProps: (record: SysLogEntity) => ({
      name: record.id,
    }),
  };

  // 格式化耗时
  const renderTimeCost = (ms?: number) => {
    if (ms === undefined || ms === null) return '-';
    if (ms > 1500) {
      return <Tag color="red">{ms} ms</Tag>;
    }
    if (ms > 500) {
      return <Tag color="orange">{ms} ms</Tag>;
    }
    return <Tag color="green">{ms} ms</Tag>;
  };

  // 全量列定义
  const allColumns: ColumnsType<SysLogEntity> = useMemo(
    () => [
      {
        title: '操作用户',
        dataIndex: 'loginName',
        key: 'loginName',
        width: 160,
        render: (name: string) => (
          <Space size={6}>
            <UserOutlined style={{ color: '#1677ff' }} />
            <span style={{ fontWeight: 600, color: 'var(--ap-text)' }}>{name || '匿名'}</span>
          </Space>
        ),
      },
      {
        title: '客户端 IP',
        dataIndex: 'ip',
        key: 'ip',
        width: 140,
        render: (ip: string) => <Tag color="blue">{ip || '-'}</Tag>,
      },
      {
        title: '终端环境',
        key: 'clientInfo',
        width: 200,
        render: (_, record: SysLogEntity) => (
          <Space size={4}>
            <Tag>{record.os || '未知系统'}</Tag>
            <Tag color="geekblue">{record.browser || '未知浏览器'}</Tag>
          </Space>
        ),
      },
      {
        title: '调用接口 / 方法',
        dataIndex: 'method',
        key: 'method',
        ellipsis: true,
        render: (method: string) => (
          <span style={{ fontFamily: 'monospace', fontSize: 12, color: 'var(--ap-text-2)' }}>
            {method || '-'}
          </span>
        ),
      },
      {
        title: '执行耗时',
        dataIndex: 'time',
        key: 'time',
        width: 120,
        align: 'center',
        render: (t: number) => renderTimeCost(t),
      },
      {
        title: '访问时间',
        dataIndex: 'createdDate',
        key: 'createdDate',
        width: 180,
        render: (date: string) =>
          date ? (
            <Space size={4} style={{ color: 'var(--ap-text-3)', fontSize: 13 }}>
              <ClockCircleOutlined />
              <span>{date}</span>
            </Space>
          ) : (
            '-'
          ),
      },
      {
        title: '操作',
        key: 'action',
        width: 90,
        fixed: 'right',
        align: 'center',
        render: (_, record: SysLogEntity) => (
          <Button
            size="small"
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
          >
            详情
          </Button>
        ),
      },
    ],
    []
  );

  const columns = useMemo(() => {
    return allColumns.filter((col) => !col.key || visibleColumns.includes(col.key as string));
  }, [allColumns, visibleColumns]);

  // 列配置 Popover
  const columnSettingContent = (
    <div style={{ width: 180 }}>
      <div className="crud-col-popover-title">
        <span>展示列配置</span>
        <Button
          type="link"
          size="small"
          style={{ padding: 0 }}
          onClick={() =>
            setVisibleColumns([
              'loginName',
              'ip',
              'clientInfo',
              'method',
              'time',
              'createdDate',
              'action',
            ])
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
    fetchSysLogList();
  }, []);

  return (
    <div className="crud-container fade-in">
      {/* 顶部查询卡片 */}
      <div className="crud-query-card">
        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={12} md={8}>
              <Form.Item name="condition" label="综合检索条件">
                <Input
                  placeholder="请输入登录名 / IP / 描述等"
                  prefix={<SearchOutlined style={{ color: 'var(--ap-text-3)' }} />}
                  allowClear
                />
              </Form.Item>
            </Col>

            <Col xs={24} sm={12} md={10}>
              <Form.Item label="访问时间范围">
                <Space size={8} wrap>
                  <RangePicker
                    value={dateRange}
                    onChange={(val) => setDateRange(val as any)}
                    showTime
                    format="YYYY-MM-DD HH:mm:ss"
                    style={{ width: 340 }}
                  />
                  <Button size="small" onClick={() => handleQuickDate(0)}>
                    今日
                  </Button>
                  <Button size="small" onClick={() => handleQuickDate(3)}>
                    近3天
                  </Button>
                  <Button size="small" onClick={() => handleQuickDate(7)}>
                    近7天
                  </Button>
                </Space>
              </Form.Item>
            </Col>

            <Col
              xs={24}
              sm={24}
              md={6}
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

      {/* 日志表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
            <Button
              danger
              icon={<DeleteOutlined />}
              disabled={selectedLogs.length === 0}
              onClick={handleBatchDelete}
            >
              批量删除日志
            </Button>
          </div>

          <div className="crud-toolbar-right">
            <Tooltip title="刷新数据">
              <Button
                type="text"
                className="crud-tool-btn"
                icon={<ReloadOutlined spin={loading} />}
                onClick={() => fetchSysLogList(searchForm)}
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
                <Button type="text" className="crud-tool-btn" icon={<ColumnHeightOutlined />} />
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

        {/* 批量操作浮条 */}
        {selectedRowKeys.length > 0 && (
          <div className="crud-selection-bar">
            <div className="crud-selection-bar-text">
              <InfoCircleFilled />
              <span>
                已选择 <strong style={{ color: '#1677ff' }}>{selectedRowKeys.length}</strong> 条日志
              </span>
              <Button
                type="link"
                size="small"
                style={{ padding: 0 }}
                onClick={() => {
                  setSelectedRowKeys([]);
                  setSelectedLogs([]);
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
          dataSource={sysLogList}
          loading={loading}
          rowKey={(record, index) => record.id || `log-${index}`}
          rowSelection={rowSelection}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (totalCount, range) =>
              `第 ${range[0]}-${range[1]} 条 / 共 ${totalCount} 条日志`,
            onChange: handlePageChange,
            onShowSizeChange: handlePageChange,
            pageSizeOptions: ['10', '20', '30', '50'],
          }}
          size={tableSize}
          locale={{
            emptyText: sysLogList.length === 0 && !loading ? '暂无匹配日志' : undefined,
          }}
        />
      </div>

      {/* 快捷日志详情抽屉 */}
      <Drawer
        title="系统审计日志详情"
        placement="right"
        width={600}
        open={drawerVisible}
        onClose={() => setDrawerVisible(false)}
        extra={
          activeLog?.id && (
            <Button
              type="primary"
              size="small"
              onClick={() => navigate(`/admin/syslog/view?id=${activeLog.id}`)}
            >
              进入完整页
            </Button>
          )
        }
      >
        {activeLog && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="登录用户">
              <strong>{activeLog.loginName || '-'}</strong> (ID: {activeLog.userId || '-'})
            </Descriptions.Item>
            <Descriptions.Item label="IP 地址">
              <Tag color="blue">{activeLog.ip || '-'}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="客户端">
              <Space>
                <Tag>{activeLog.os || '-'}</Tag>
                <Tag color="geekblue">{activeLog.browser || '-'}</Tag>
              </Space>
            </Descriptions.Item>
            <Descriptions.Item label="调用方法">
              <span style={{ fontFamily: 'monospace', fontSize: 12 }}>{activeLog.method || '-'}</span>
            </Descriptions.Item>
            <Descriptions.Item label="操作描述">
              {activeLog.description || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="耗时">
              {renderTimeCost(activeLog.time)}
            </Descriptions.Item>
            <Descriptions.Item label="记录时间">
              {activeLog.createdDate || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="请求参数">
              <pre
                style={{
                  margin: 0,
                  padding: '8px',
                  background: '#fafafa',
                  borderRadius: '4px',
                  maxHeight: '200px',
                  overflow: 'auto',
                  fontSize: 12,
                  fontFamily: 'monospace',
                }}
              >
                {activeLog.params || '-'}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="响应结果">
              <pre
                style={{
                  margin: 0,
                  padding: '8px',
                  background: '#fafafa',
                  borderRadius: '4px',
                  maxHeight: '200px',
                  overflow: 'auto',
                  fontSize: 12,
                  fontFamily: 'monospace',
                }}
              >
                {activeLog.response || '-'}
              </pre>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>
    </div>
  );
};

export default SysLogList;
