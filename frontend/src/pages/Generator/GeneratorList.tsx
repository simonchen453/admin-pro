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
  Pagination,
  Breadcrumb,
  Divider,
  Row,
  Col
} from 'antd';
import {
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  CodeOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getGeneratorListApi,
  batchGenCodeApi,
  genAllCodeApi
} from '../../api/generator';
import type {
  GeneratorEntity,
  GeneratorSearchForm,
  GeneratorListResponse
} from '../../types';

const GeneratorList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [tableData, setTableData] = useState<GeneratorEntity[]>([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchForm, setSearchForm] = useState<GeneratorSearchForm>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [selectedTables, setSelectedTables] = useState<GeneratorEntity[]>([]);

  const fetchTableList = async (searchParams?: GeneratorSearchForm, page?: number, size?: number) => {
    setLoading(true);
    try {
      const params = {
        ...(searchParams || searchForm),
        pageNo: (page ?? currentPage) - 1,
        pageSize: size ?? pageSize
      };

      const response: GeneratorListResponse = await getGeneratorListApi(params);

      if (response.restCode === '200' || response.success) {
        setTableData(response.data.records || []);
        setTotal(response.data.totalCount || 0);
      } else {
        message.error(response.message || '获取数据失败');
        setTableData([]);
        setTotal(0);
      }
    } catch (error) {
      console.error('获取数据失败:', error);
      message.error('获取数据失败');
      setTableData([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (values: GeneratorSearchForm) => {
    setSearchForm(values);
    setCurrentPage(1);
    fetchTableList(values, 1);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    setCurrentPage(1);
    fetchTableList(emptyForm, 1);
  };

  const handlePageChange = (page: number, size?: number) => {
    setCurrentPage(page);
    if (size) {
      setPageSize(size);
    }
    fetchTableList(searchForm, page, size);
  };

  const formatDateTime = (dateTime?: string) => {
    if (!dateTime) return '-';
    return dateTime;
  };

  const handleGenerate = (tableName: string) => {
    Modal.confirm({
      title: '确认生成',
      content: `确定要生成表 ${tableName} 的代码吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await batchGenCodeApi(tableName);
          message.success('代码生成成功，正在下载...');
        } catch (error) {
          console.error('代码生成失败:', error);
          message.error('代码生成失败');
        }
      }
    });
  };

  const handleBatchGenerate = () => {
    if (selectedTables.length === 0) {
      message.warning('请选择要操作的数据！');
      return;
    }

    const tables = selectedTables.map(item => item.tableName).join(',');
    
    Modal.confirm({
      title: '确认生成',
      content: `是否生成选中代码？共 ${selectedTables.length} 个表`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await batchGenCodeApi(tables);
          message.success('代码生成成功，正在下载...');
          setSelectedTables([]);
          setSelectedRowKeys([]);
        } catch (error) {
          console.error('代码生成失败:', error);
          message.error('代码生成失败');
        }
      }
    });
  };

  const handleGenerateAll = () => {
    Modal.confirm({
      title: '确认生成',
      content: '确定要生成全部代码吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await genAllCodeApi();
          message.success('代码生成成功，正在下载...');
        } catch (error) {
          console.error('代码生成失败:', error);
          message.error('代码生成失败');
        }
      }
    });
  };

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys: React.Key[], selectedRows: GeneratorEntity[]) => {
      setSelectedRowKeys(selectedRowKeys);
      setSelectedTables(selectedRows);
    },
    getCheckboxProps: (record: GeneratorEntity) => ({
      name: record.tableName,
    }),
  };

  const columns: ColumnsType<GeneratorEntity> = [
    {
      title: 'NO.',
      key: 'index',
      width: 60,
      render: (_, __, index) => (currentPage - 1) * pageSize + index + 1
    },
    {
      title: '表名',
      dataIndex: 'tableName',
      key: 'tableName',
      ellipsis: true,
      minWidth: 200
    },
    {
      title: '表描述',
      dataIndex: 'tableComment',
      key: 'tableComment',
      ellipsis: true,
      minWidth: 200
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 180,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '更新时间',
      dataIndex: 'updatedDate',
      key: 'updatedDate',
      width: 180,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record: GeneratorEntity) => (
        <Space size="small">
          <Button
            size="small"
            type="primary"
            icon={<CodeOutlined />}
            onClick={() => handleGenerate(record.tableName)}
          >
            生成代码
          </Button>
        </Space>
      )
    }
  ];

  useEffect(() => {
    fetchTableList({}, 1);
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
              title: '代码生成器'
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
                <Form.Item name="tableName" label="表名">
                  <Input placeholder="请输入表名" allowClear />
                </Form.Item>
              </Col>
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
            <Button 
              type="primary" 
              icon={<CodeOutlined />}
              disabled={selectedTables.length === 0}
              onClick={handleBatchGenerate}
            >
              批量生成
            </Button>
            <Button 
              type="primary" 
              icon={<CodeOutlined />}
              onClick={handleGenerateAll}
            >
              生成全部
            </Button>
          </Space>
          <div>
            已选择 {selectedTables.length} 项
          </div>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={tableData}
            loading={loading}
            rowKey={(record) => record.tableName}
            rowSelection={rowSelection}
            pagination={false}
            size="small"
            locale={{
              emptyText: tableData.length === 0 && !loading ? '暂无数据' : undefined
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

export default GeneratorList;

