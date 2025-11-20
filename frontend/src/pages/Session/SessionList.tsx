import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Select,
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
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  StopOutlined,
  PlayCircleOutlined,
  LogoutOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getSessionListApi,
  suspendSessionApi,
  unsuspendSessionApi,
  killSessionApi
} from '../../api/session';
import { getDomainListApi } from '../../api/user';
import type {
  SessionEntity,
  SessionSearchForm
} from '../../types';

const { Option } = Select;

const SessionList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [sessionList, setSessionList] = useState<SessionEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<SessionSearchForm>({});
  const [domainList, setDomainList] = useState<Array<{ id: string; name: string; display: string }>>([]);

  useEffect(() => {
    fetchDomainList();
    fetchSessionList();
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

  const fetchSessionList = async (searchParams?: SessionSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response = await getSessionListApi(params);

      if (response.restCode === '200') {
        setSessionList(response.data.records || []);
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '加载数据失败');
        setSessionList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取Session列表失败:', error);
      message.error('加载数据失败');
      setSessionList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: SessionSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchSessionList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm: SessionSearchForm = {
      sessionId: undefined,
      status: undefined,
      userDomain: undefined,
      loginName: undefined,
      ipAddr: undefined,
      deptNo: undefined
    };
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchSessionList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    const newPageSize = size ?? pageSize;
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchSessionList(undefined, page, newPageSize);
  };

  const handleSuspend = (row: SessionEntity) => {
    Modal.confirm({
      title: '确认限制',
      content: `你确定要限制（${row.loginName}）的访问吗？`,
      onOk: async () => {
        try {
          const response = await suspendSessionApi(row.id);
          if (response.restCode === '200') {
            message.success('限制Session成功');
            fetchSessionList();
          } else {
            message.error(response.message || '限制Session失败');
          }
        } catch (error) {
          console.error('限制Session失败:', error);
          message.error('限制Session失败');
        }
      }
    });
  };

  const handleUnSuspend = (row: SessionEntity) => {
    Modal.confirm({
      title: '确认解除限制',
      content: `你确定要解除（${row.loginName}）的访问吗？`,
      onOk: async () => {
        try {
          const response = await unsuspendSessionApi(row.id);
          if (response.restCode === '200') {
            message.success('解除限制成功');
            fetchSessionList();
          } else {
            message.error(response.message || '解除限制失败');
          }
        } catch (error) {
          console.error('解除限制失败:', error);
          message.error('解除限制失败');
        }
      }
    });
  };

  const handleKill = (row: SessionEntity) => {
    Modal.confirm({
      title: '确认强退',
      content: `你确定要强退（${row.loginName}）吗？`,
      onOk: async () => {
        try {
          const response = await killSessionApi(row.id);
          if (response.restCode === '200') {
            message.success('强退Session成功');
            fetchSessionList();
          } else {
            message.error(response.message || '强退Session失败');
          }
        } catch (error) {
          console.error('强退Session失败:', error);
          message.error('强退Session失败');
        }
      }
    });
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      active: { color: 'green', text: '正常' },
      suspend: { color: 'orange', text: '限制' },
      killed: { color: 'red', text: '已强退' }
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  const formatDateTime = (dateTime?: string) => {
    if (!dateTime) return '-';
    return dateTime;
  };

  const columns: ColumnsType<SessionEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: 'Session ID',
      dataIndex: 'sessionId',
      key: 'sessionId',
      ellipsis: true,
      width: 200
    },
    {
      title: '用户域',
      dataIndex: 'userDomain',
      key: 'userDomain',
      width: 120,
      render: (userDomain: string) => {
        const domain = domainList.find(d => d.name === userDomain || d.id === userDomain);
        return domain ? domain.display : userDomain;
      }
    },
    {
      title: '登陆名',
      dataIndex: 'loginName',
      key: 'loginName',
      ellipsis: true,
      width: 120
    },
    {
      title: '部门编号',
      dataIndex: 'deptNo',
      key: 'deptNo',
      ellipsis: true,
      width: 120
    },
    {
      title: '登陆IP',
      dataIndex: 'ipAddr',
      key: 'ipAddr',
      ellipsis: true,
      width: 140
    },
    {
      title: '登陆地址',
      dataIndex: 'loginLocation',
      key: 'loginLocation',
      ellipsis: true,
      width: 150
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      key: 'browser',
      ellipsis: true,
      width: 120
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      key: 'os',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '登陆时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 180,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          {record.status === 'active' && (
            <Button
              size="small"
              type="primary"
              icon={<StopOutlined />}
              onClick={() => handleSuspend(record)}
            >
              限制
            </Button>
          )}
          {record.status === 'suspend' && (
            <Button
              size="small"
              type="primary"
              icon={<PlayCircleOutlined />}
              onClick={() => handleUnSuspend(record)}
            >
              解除
            </Button>
          )}
          {(record.status === 'active' || record.status === 'suspend') && (
            <Button
              size="small"
              type="primary"
              danger
              icon={<LogoutOutlined />}
              onClick={() => handleKill(record)}
            >
              强退
            </Button>
          )}
        </Space>
      )
    }
  ];

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
              title: '在线用户管理'
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
                <Form.Item name="userDomain" label="用户域">
                  <Select placeholder="请选择用户域" allowClear style={{ width: '100%' }}>
                    {domainList.map(domain => (
                      <Option key={domain.name} value={domain.name}>
                        {domain.display}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="loginName" label="登陆名">
                  <Input placeholder="请输入登陆名" allowClear/>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Select placeholder="请选择" allowClear style={{ width: '100%' }}>
                    <Option value="active">正常</Option>
                    <Option value="suspend">限制</Option>
                    <Option value="killed">已强退</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="deptNo" label="部门编号">
                  <Input placeholder="请输入部门编号" />
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
                  <Button onClick={() => fetchSessionList()} icon={<ReloadOutlined />}>
                    刷新
                  </Button>
                </Space>
              </Col>
            </Row>
          </Form>
        </Card>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={sessionList}
            loading={loading}
            rowKey="id"
            pagination={false}
            size="small"
            scroll={{ x: 1500 }}
            locale={{
              emptyText: sessionList.length === 0 && !loading ? '暂无数据' : undefined
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

export default SessionList;

