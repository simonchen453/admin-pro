import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Form,
  Input,
  Select,
  Card,
  message,
  Modal,
  Pagination,
  Breadcrumb,
  Divider,
  Row,
  Col
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
  get${className}ListApi,
  delete${className}Api
} from '../../api/${classname}';
import type {
  ${className}Entity,
  ${className}SearchForm,
  ${className}ListResponse
} from '../../types';
import ${className}Form from './${className}Form';

const { Option } = Select;

const ${className}List: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [tableData, setTableData] = useState<${className}Entity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<${className}SearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedRows, setSelectedRows] = useState<${className}Entity[]>([]);
  
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<${className}Entity | null>(null);
  const [formKey, setFormKey] = useState(0);

  const fetchList = async (params: ${className}SearchForm = {}) => {
    setLoading(true);
    try {
      const response = await get${className}ListApi({
        ...params,
        pageNo: (params.pageNo ?? currentPage) - 1,
        pageSize: params.pageSize ?? pageSize
      });
      
      const responseData = response as any;
      const list = responseData?.list || responseData?.records || responseData?.data?.list || responseData?.data?.records || [];
      const total = responseData?.pagination?.total || responseData?.totalCount || responseData?.data?.pagination?.total || responseData?.data?.totalCount || 0;
      
      setTableData(list);
      setTotal(total);
    } catch (error) {
      console.error('获取数据失败:', error);
      message.error('获取数据失败');
      setTableData([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: ${className}SearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchList(values);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchList(emptyForm);
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchList({ ...searchForm, pageNo: page, pageSize: size || pageSize });
  };

  const handleCreate = () => {
    setEditingRecord(null);
    setFormKey(prev => prev + 1);
    setIsModalVisible(true);
  };

  const handleEdit = (record: ${className}Entity) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = (record: ${className}Entity) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除这条记录吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const id = record.${primaryKey.attrname} || (record as any).id;
          await delete${className}Api(id);
          message.success('删除成功');
          fetchList(searchForm);
        } catch (error) {
          console.error('删除失败:', error);
          message.error('删除失败');
        }
      }
    });
  };

  const handleBatchDelete = () => {
    if (selectedRows.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    
    Modal.confirm({
      title: '确认删除',
      content: `<#noparse>`确定要删除选中的 ${selectedRows.length} 条记录吗？`</#noparse>`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const ids = selectedRows.map(row => row.${primaryKey.attrname} || (row as any).id).join(',');
          await delete${className}Api(ids);
          message.success('删除成功');
          setSelectedRows([]);
          setSelectedRowKeys([]);
          fetchList(searchForm);
        } catch (error) {
          console.error('删除失败:', error);
          message.error('删除失败');
        }
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: ${className}Entity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedRows(selectedRows);
    }
  };

  const columns: ColumnsType<${className}Entity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
    {
      title: '${column.columnComment}',
      dataIndex: '${column.attrname}',
      key: '${column.attrname}',
      ellipsis: true
    },
</#if>
</#list>
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 200,
      render: (_, record: ${className}Entity) => (
        <Space size="small">
          <Button
            size="small"
            type="primary"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            修改
          </Button>
          <Button
            size="small"
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record)}
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    fetchList();
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
              title: '${tableComment}管理'
            }
          ]}
        />
      </div>
      
      <Divider />

      <Card>
        <Card size="small" style={{ marginBottom: 16 }}>
          <Form autoComplete="off"
            form={form}
            layout="inline"
            onFinish={handleSearch}
          >
            <Row gutter={[16, 16]} style={{ width: '100%' }}>
<#list entityColumns as column>
<#if column.attrname != primaryKey.attrname>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="${column.attrname}" label="${column.columnComment}">
<#if column.attrname == 'status'>
                  <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                    <Option value="ACTIVE">正常</Option>
                    <Option value="LOCK">锁定</Option>
                  </Select>
<#else>
                  <Input placeholder="请输入${column.columnComment}" allowClear />
</#if>
                </Form.Item>
              </Col>
</#if>
</#list>
            </Row>
            <Row>
              <Col span={24} style={{ textAlign: 'left', marginTop: 16, marginLeft: 2, marginBottom: 10 }}>
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
              disabled={selectedRows.length === 0}
              onClick={handleBatchDelete}
            >
              删除
            </Button>
          </Space>
          <div>
            已选择 <#noparse>{selectedRows.length}</#noparse> 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={tableData}
            loading={loading}
            rowKey={(record) => record.${primaryKey.attrname} || (record as any).id}
            rowSelection={rowSelection}
            pagination={false}
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
            showTotal={<#noparse>(total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`</#noparse>}
            onChange={handlePageChange}
            onShowSizeChange={handlePageChange}
            pageSizeOptions={['10', '20', '30', '50']}
          />
        </div>
      </Card>

      <Modal
        title={editingRecord ? '编辑${tableComment}' : '新增${tableComment}'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingRecord(null);
        }}
        footer={null}
        width={800}
      >
        <${className}Form
          key={editingRecord ? 'edit-' + editingRecord.${primaryKey.attrname} : 'new-' + formKey}
          record={editingRecord}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingRecord(null);
            fetchList(searchForm);
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingRecord(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default ${className}List;

