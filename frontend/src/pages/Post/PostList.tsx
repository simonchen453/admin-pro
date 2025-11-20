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
  deletePostApi,
  getPostListApi
} from '../../api/post';
import {
  PostStatus,
  type PostEntity,
  type PostSearchForm,
  type PostListResponse
} from '../../types';
import PostForm from './PostForm';

const { Option } = Select;

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
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPost, setEditingPost] = useState<PostEntity | null>(null);

  const fetchPostList = async (searchParams?: PostSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response: PostListResponse = await getPostListApi(params);

      if (response.restCode === '200') {
        setPostList(response.data.records || []);
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
    setSearchForm(values);
    setCurrentPage(1);
    fetchPostList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchPostList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    const newPageSize = size ?? pageSize;
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchPostList(undefined, page, newPageSize);
  };

  const handleEdit = (post: PostEntity) => {
    setEditingPost(post);
    setIsModalVisible(true);
  };

  const handleCreate = () => {
    setEditingPost(null);
    setIsModalVisible(true);
  };

  const handleBatchDelete = () => {
    if (selectedPosts.length === 0) {
      message.warning('请选择要删除的岗位');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除选中的 ${selectedPosts.length} 个岗位吗？`,
      onOk: async () => {
        try {
          const validIds = selectedPosts
            .map(post => post.id)
            .filter((id): id is string => id !== undefined);

          if (validIds.length === 0) {
            message.error('选中的岗位中没有有效的ID');
            return;
          }

          const ids = validIds.join(',');
          const response = await deletePostApi(ids);
          if (response.restCode === '200') {
            message.success('批量删除成功');
            setSelectedPosts([]);
            setSelectedRowKeys([]);
            fetchPostList();
          } else {
            message.error(response.message || '批量删除失败');
          }
        } catch (error) {
          console.error('批量删除失败:', error);
          message.error('批量删除失败');
        }
      }
    });
  };

  const handleDelete = (post: PostEntity) => {
    if (!post.id) {
      message.error('岗位ID不存在');
      return;
    }

    Modal.confirm({
      title: '确认删除',
      content: `确定要删除岗位 "${post.name}" 吗？`,
      okText: '确认',
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
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: PostEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedPosts(selectedRows);
    },
    getCheckboxProps: (record: PostEntity) => ({
      name: record.name,
    }),
  };

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      [PostStatus.ACTIVE]: { color: 'green', text: '正常' },
      [PostStatus.INACTIVE]: { color: 'red', text: '停用' }
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  const formatDate = (date?: string) => {
    if (!date) return '-';
    return new Date(date).toLocaleString('zh-CN');
  };

  const columns: ColumnsType<PostEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '岗位编码',
      dataIndex: 'code',
      key: 'code',
      ellipsis: true
    },
    {
      title: '岗位名称',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true
    },
    {
      title: '岗位排序',
      dataIndex: 'sort',
      key: 'sort',
      align: 'center',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      width: 100,
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      align: 'center',
      width: 180,
      render: (date: string) => formatDate(date)
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record: PostEntity) => (
        <Space size="small">
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            type="primary"
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

  useEffect(() => {
    fetchPostList();
  }, []);

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
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
            title: '岗位管理'
          }
        ]}
      />
      
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
                <Form.Item name="code" label="岗位编码">
                  <Input placeholder="请输入岗位编码" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="name" label="岗位名称">
                  <Input placeholder="请输入岗位名称" allowClear />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                    <Option value={PostStatus.ACTIVE}>正常</Option>
                    <Option value={PostStatus.INACTIVE}>停用</Option>
                  </Select>
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
              danger 
              icon={<DeleteOutlined />} 
              disabled={selectedPosts.length === 0}
              onClick={handleBatchDelete}
            >
              删除
            </Button>
          </Space>
          <div>
            已选择 {selectedPosts.length} 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={postList}
            loading={loading}
            rowKey={(record) => record.id || `post-${record.code}`}
            rowSelection={rowSelection}
            pagination={false}
            scroll={{ x: 800 }}
            size="small"
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
        title={editingPost ? '编辑岗位' : '新增岗位'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingPost(null);
        }}
        footer={null}
        width={600}
        destroyOnClose
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

