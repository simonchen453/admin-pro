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
  Row,
  Col
} from 'antd';
import {
  SearchOutlined,
  ClearOutlined,
  CodeOutlined,
  DownloadOutlined
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
        pageNo: page ?? currentPage,
        pageSize: size ?? pageSize
      };

      const response: GeneratorListResponse = await getGeneratorListApi(params);

      if (response.restCode === '200' || response.success) {
        setTableData((response.data.records || []).map((item: any, index: number) => ({ ...item, index })));
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

  const handleGenerate = (record: GeneratorEntity) => {
    Modal.confirm({
      title: '确认生成',
      content: `确定要生成表 ${record.tableName} 的代码吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await batchGenCodeApi(record.tableName);
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
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 60,
      render: (value: number) => (currentPage - 1) * pageSize + value + 1
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
            type="link"
            icon={<DownloadOutlined />}
            onClick={() => handleGenerate(record)}
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
    <div className="fade-in" style={{ padding: '0' }}>


      <Card className="modern-card" styles={{ body: { padding: '24px' } }}>
        <Form autoComplete="off"
          form={form}
          layout="inline"
          onFinish={handleSearch}
          style={{ marginBottom: 24 }}
        >
          <Row gutter={[16, 16]} style={{ width: '100%' }}>
            <Col xs={24} sm={12} md={8}>
              <Form.Item name="tableName" style={{ marginBottom: 0 }}>
                <Input placeholder="表名" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8}>
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

        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
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
        </div>

        <div className="modern-table">
          <Table
            columns={columns}
            dataSource={tableData}
            loading={loading}
            rowKey={(record) => record.tableName}
            rowSelection={rowSelection}
            pagination={false}
            size="middle"
            locale={{
              emptyText: tableData.length === 0 && !loading ? '暂无数据' : undefined
            }}
          />
        </div>

        <div style={{ marginTop: 24, textAlign: 'right' }}>
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
