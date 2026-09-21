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
  ReloadOutlined,
  ColumnHeightOutlined,
  SettingOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
  InfoCircleFilled,
  IdcardOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  deletePostApi,
  batchDeletePostApi,
  getPostListApi,
} from '../../api/post';
import {
  PostStatus,
  type PostEntity,
  type PostSearchForm,
  type PostListResponse,
} from '../../types';
import PostForm from './PostForm';
import '../../styles/crud.css';

const PostList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [postList, setPostList] = useState<PostEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<PostSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedPosts, setSelectedPosts] = useState<PostEntity[]>([]);

  // ProTable 增强状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [statusTab, setStatusTab] = useState<string>('ALL');
  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'postInfo',
    'sort',
    'status',
    'remark',
    'action',
  ]);

  const tableContainerRef = useRef<HTMLDivElement>(null);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPost, setEditingPost] = useState<PostEntity | null>(null);

  const fetchPostList = async (searchParams?: PostSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize,
      };

      const response: PostListResponse = await getPostListApi(params);

      if (response.restCode === '200') {
        setPostList(
          (response.data.records || []).map((item: any, index: number) => ({ ...item, index }))
        );
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取岗位列表失败');
        setPostList([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取岗位列表失败:', error);
      message.error('获取岗位列表失败');
      setPostList([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: PostSearchForm) => {
    const query = {
      ...values,
      status: statusTab === 'ALL' ? values.status : statusTab,
    };
    setSearchForm(query);
    setCurrentPage(1);
    fetchPostList(query, 1);
  };

  const handleReset = () => {
    form.resetFields();
    setStatusTab('ALL');
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchPostList(emptyForm, 1);
  };

  const handleStatusTabChange = (val: string | number) => {
    const tab = String(val);
    setStatusTab(tab);
    setCurrentPage(1);
    const newStatus = tab === 'ALL' ? undefined : tab;
    const newForm = { ...searchForm, status: newStatus };
    setSearchForm(newForm);
    fetchPostList(newForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    const newPageSize = size ?? pageSize;
    setCurrentPage(page);
    setPageSize(newPageSize);
    fetchPostList(searchForm, page, newPageSize);
  };

  const handleEdit = (post: PostEntity) => {
    setEditingPost(post);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingPost(null);
    setIsModalVisible(true);
  };

  const handleDelete = (post: PostEntity) => {
    if (!post.id) {
      message.error('岗位ID不存在');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除岗位「${post.name}」吗？`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deletePostApi(post.id!);
          if (response.restCode === '200') {
            message.success('删除成功');
            fetchPostList();
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
    if (selectedPosts.length === 0) {
      message.warning('请选择要删除的岗位');
      return;
    }

    const ids = selectedPosts.map((post) => post.id).filter(Boolean).join(',');

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedPosts.length} 个岗位吗？`,
      okText: '确定删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await batchDeletePostApi(ids);
          if (response.restCode === '200') {
            message.success('批量删除成功');
            setSelectedRowKeys([]);
            setSelectedPosts([]);
            fetchPostList();
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

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[], rows: PostEntity[]) => {
      setSelectedRowKeys(keys);
      setSelectedPosts(rows);
    },
    getCheckboxProps: (record: PostEntity) => ({
      name: record.name,
    }),
  };

  const allColumns: ColumnsType<PostEntity> = useMemo(
    () => [
      {
        title: '岗位信息',
        key: 'postInfo',
        width: 260,
        render: (_, record: PostEntity) => (
          <div className="crud-user-cell">
            <div
              style={{
                width: 36,
                height: 36,
                borderRadius: 8,
                background: '#e6f4ff',
                color: '#1677ff',
                display: 'grid',
                placeItems: 'center',
                fontSize: 16,
                flex: 'none',
              }}
            >
              <IdcardOutlined />
            </div>
            <div className="crud-user-meta">
              <span className="crud-user-name">{record.name}</span>
              <span className="crud-user-login">{record.code}</span>
            </div>
          </div>
        ),
      },
      {
        title: '显示顺序',
        dataIndex: 'sort',
        key: 'sort',
        width: 100,
        align: 'center',
        render: (sort: number) => <Tag>{sort ?? 0}</Tag>,
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: 110,
        align: 'center',
        render: (status: string) =>
          status === PostStatus.ACTIVE || status === '0' ? (
            <Badge status="success" text="正常启用" />
          ) : (
            <Badge status="error" text="已停用" />
          ),
      },
      {
        title: '备注说明',
        dataIndex: 'remark',
        key: 'remark',
        ellipsis: true,
        render: (text: string) => text || '-',
      },
      {
        title: '操作',
        key: 'action',
        width: 140,
        fixed: 'right',
        render: (_, record: PostEntity) => (
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
          onClick={() => setVisibleColumns(['postInfo', 'sort', 'status', 'remark', 'action'])}
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
    fetchPostList();
  }, []);

  return (
    <div className="crud-container fade-in">
      {/* 顶部查询卡片 */}
      <div className="crud-query-card">
        <div className="crud-filter-tabs">
          <Segmented
            value={statusTab}
            onChange={handleStatusTabChange}
            options={[
              { label: '全部岗位', value: 'ALL' },
              { label: '正常启用', value: PostStatus.ACTIVE },
              { label: '已停用', value: PostStatus.INACTIVE },
            ]}
          />
        </div>

        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={12} md={8}>
              <Form.Item name="code" label="岗位编码">
                <Input
                  placeholder="请输入岗位编码 (如 ceo)"
                  allowClear
                  prefix={<SearchOutlined style={{ color: 'var(--ap-text-3)' }} />}
                />
              </Form.Item>
            </Col>

            <Col xs={24} sm={12} md={8}>
              <Form.Item name="name" label="岗位名称">
                <Input
                  placeholder="请输入岗位名称 (如 董事长)"
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

      {/* 岗位表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增岗位
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              disabled={selectedPosts.length === 0}
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
                onClick={() => fetchPostList(searchForm)}
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
                已选择 <strong style={{ color: '#1677ff' }}>{selectedRowKeys.length}</strong> 个岗位
              </span>
              <Button
                type="link"
                size="small"
                style={{ padding: 0 }}
                onClick={() => {
                  setSelectedRowKeys([]);
                  setSelectedPosts([]);
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
          dataSource={postList}
          loading={loading}
          rowKey={(record) => record.id || `post-${record.code}`}
          rowSelection={rowSelection}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (totalCount, range) =>
              `第 ${range[0]}-${range[1]} 条 / 共 ${totalCount} 条岗位`,
            onChange: handlePageChange,
            onShowSizeChange: handlePageChange,
            pageSizeOptions: ['10', '20', '30', '50'],
          }}
          size={tableSize}
          locale={{
            emptyText: postList.length === 0 && !loading ? '暂无匹配岗位' : undefined,
          }}
        />
      </div>

      <Modal
        title={editingPost ? '编辑岗位' : '新增岗位'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingPost(null);
        }}
        footer={null}
        width={600}
        destroyOnHidden
      >
        <PostForm
          post={editingPost}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingPost(null);
            fetchPostList();
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingPost(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default PostList;
