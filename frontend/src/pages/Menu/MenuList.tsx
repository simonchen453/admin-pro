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
  HomeOutlined,
  ExpandOutlined,
  CompressOutlined
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getMenuTreeListApi,
  getMenuDetailApi,
  deleteMenuApi,
  getMenuTreeSelectApi
} from '../../api/menu';
import type {
  MenuEntity,
  MenuSearchForm,
  MenuTreeSelectNode
} from '../../types';
import { MenuStatus, MenuType, MenuVisible } from '../../types';
import MenuForm from './MenuForm';

const { Option } = Select;

const MenuList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [menuList, setMenuList] = useState<MenuEntity[]>([]);
  const [searchForm, setSearchForm] = useState<MenuSearchForm>({});
  
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingMenu, setEditingMenu] = useState<MenuEntity | null>(null);
  const [menuOptions, setMenuOptions] = useState<MenuTreeSelectNode[]>([]);
  const [formKey, setFormKey] = useState(0);
  const [expandedRowKeys, setExpandedRowKeys] = useState<React.Key[]>([]);

  const handleTree = (data: MenuEntity[], id: string, parentId: string | number = 0): MenuEntity[] => {
    const tree: MenuEntity[] = [];
    data.forEach((item) => {
      const itemParentId = typeof item.parentId === 'string' ? item.parentId : String(item.parentId);
      const compareParentId = typeof parentId === 'string' ? parentId : String(parentId);
      if (itemParentId === compareParentId || itemParentId === '0' && parentId === 0) {
        const children = handleTree(data, id, item.id || 0);
        if (children.length > 0) {
          item.children = children;
        }
        tree.push(item);
      }
    });
    return tree;
  };

  const getAllRowKeys = (data: MenuEntity[]): React.Key[] => {
    const keys: React.Key[] = [];
    const traverse = (items: MenuEntity[]) => {
      items.forEach(item => {
        if (item.id) {
          keys.push(item.id);
        }
        if (item.children && item.children.length > 0) {
          traverse(item.children);
        }
      });
    };
    traverse(data);
    return keys;
  };

  const fetchMenuList = async (params?: MenuSearchForm) => {
    setLoading(true);
    try {
      const response = await getMenuTreeListApi(params || searchForm);
      if (response.restCode === '200') {
        const treeData = handleTree(response.data || [], 'id', 0);
        setMenuList(treeData);
        const allKeys = getAllRowKeys(treeData);
        setExpandedRowKeys(allKeys);
      } else {
        message.error(response.message || '获取菜单列表失败');
        setMenuList([]);
        setExpandedRowKeys([]);
      }
    } catch (error) {
      console.error('获取菜单列表失败:', error);
      message.error('获取菜单列表失败');
      setMenuList([]);
      setExpandedRowKeys([]);
    } finally {
      setLoading(false);
    }
  };

  const convertMenuEntityToTreeSelect = (menus: MenuEntity[]): MenuTreeSelectNode[] => {
    return menus.map(menu => ({
      id: menu.id || '',
      display: menu.display,
      children: menu.children ? convertMenuEntityToTreeSelect(menu.children) : undefined
    }));
  };

  const fetchMenuTreeSelect = async () => {
    try {
      const response = await getMenuTreeSelectApi();
      if (response.restCode === '200') {
        const menuList = response.data || [];
        const treeData = handleTree(menuList as MenuEntity[], 'id', 0);
        const rootMenu: MenuTreeSelectNode = {
          id: '0',
          display: '主类目',
          children: convertMenuEntityToTreeSelect(treeData)
        };
        setMenuOptions([rootMenu]);
      } else {
        message.error(response.message || '获取菜单树失败');
        setMenuOptions([]);
      }
    } catch (error) {
      console.error('获取菜单树失败:', error);
      setMenuOptions([]);
    }
  };

  const handleSearch = (values: MenuSearchForm) => {
    setSearchForm(values);
    fetchMenuList(values);
  };

  const handleReset = () => {
    form.resetFields();
    const emptyForm = {};
    setSearchForm(emptyForm);
    fetchMenuList(emptyForm);
  };

  const handleCreate = () => {
    setEditingMenu(null);
    setFormKey(prev => prev + 1);
    fetchMenuTreeSelect();
    setIsModalVisible(true);
  };

  const handleAdd = (row?: MenuEntity) => {
    const newMenu: MenuEntity = {
      parentId: row?.id || 0,
      display: '',
      name: '',
      type: MenuType.DIRECTORY,
      status: MenuStatus.ACTIVE,
      visible: MenuVisible.SHOW,
      orderNum: 0
    };
    setEditingMenu(newMenu);
    setFormKey(prev => prev + 1);
    fetchMenuTreeSelect();
    setIsModalVisible(true);
  };

  const handleEdit = (row: MenuEntity) => {
    if (!row.id) {
      message.error('菜单ID不存在');
      return;
    }
    setLoading(true);
    getMenuDetailApi(row.id)
      .then(response => {
        if (response.restCode === '200') {
          setEditingMenu(response.data);
          setFormKey(prev => prev + 1);
          fetchMenuTreeSelect();
          setIsModalVisible(true);
        } else {
          message.error(response.message || '获取菜单详情失败');
        }
      })
      .catch(error => {
        console.error('获取菜单详情失败:', error);
        message.error('获取菜单详情失败');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleDelete = (row: MenuEntity) => {
    if (!row.id) {
      message.error('菜单ID不存在');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `是否确认删除名称为"${row.display}"的数据项?`,
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const response = await deleteMenuApi(row.id!);
          if (response.restCode === '200') {
            message.success('删除成功');
            fetchMenuList();
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

  const getStatusTag = (status: string) => {
    const statusMap: Record<string, { color: string; text: string }> = {
      [MenuStatus.ACTIVE]: { color: 'green', text: '正常' },
      [MenuStatus.INACTIVE]: { color: 'red', text: '停用' }
    };
    const statusInfo = statusMap[status] || { color: 'default', text: status };
    return <Tag color={statusInfo.color}>{statusInfo.text}</Tag>;
  };

  const formatDateTime = (dateStr?: string) => {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch {
      return dateStr;
    }
  };

  const isAllExpanded = () => {
    const allKeys = getAllRowKeys(menuList);
    return allKeys.length > 0 && allKeys.length === expandedRowKeys.length && 
           allKeys.every(key => expandedRowKeys.includes(key));
  };

  const handleToggleExpand = () => {
    if (isAllExpanded()) {
      setExpandedRowKeys([]);
    } else {
      const allKeys = getAllRowKeys(menuList);
      setExpandedRowKeys(allKeys);
    }
  };

  const columns: ColumnsType<MenuEntity> = [
    {
      title: '菜单显示名称',
      dataIndex: 'display',
      key: 'display',
      width: 160,
      ellipsis: true
    },
    {
      title: '菜单名称',
      dataIndex: 'name',
      key: 'name',
      width: 250,
      ellipsis: true
    },
    {
      title: '图标',
      dataIndex: 'icon',
      key: 'icon',
      align: 'center',
      width: 100,
      render: (icon: string) => icon ? <i className={icon} /> : '-'
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      key: 'orderNum',
      width: 60,
      align: 'center'
    },
    {
      title: '权限标识',
      dataIndex: 'permission',
      key: 'permission',
      ellipsis: true
    },
    {
      title: '组件路径',
      dataIndex: 'url',
      key: 'url',
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      align: 'center',
      render: (status: string) => getStatusTag(status)
    },
    {
      title: '创建时间',
      dataIndex: 'createdDate',
      key: 'createdDate',
      width: 150,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_, record: MenuEntity) => (
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
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record)}
            type="primary"
          >
            新增
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
    fetchMenuList();
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
            title: '菜单管理'
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
                <Form.Item name="name" label="菜单名称">
                  <Input 
                    placeholder="请输入菜单名称" 
                    allowClear
                    onPressEnter={() => form.submit()}
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={6}>
                <Form.Item name="status" label="状态">
                  <Select placeholder="菜单状态" allowClear style={{ width: '100%' }}>
                    <Option value={MenuStatus.ACTIVE}>正常</Option>
                    <Option value={MenuStatus.INACTIVE}>停用</Option>
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
              icon={isAllExpanded() ? <CompressOutlined /> : <ExpandOutlined />} 
              onClick={handleToggleExpand}
            >
              {isAllExpanded() ? '收起全部' : '展开全部'}
            </Button>
          </Space>
        </div>

        <div style={{ background: 'white', borderRadius: '8px', overflow: 'hidden' }}>
          <Table
            columns={columns}
            dataSource={menuList}
            loading={loading}
            rowKey={(record) => record.id || `menu-${record.name}`}
            pagination={false}
            expandedRowKeys={expandedRowKeys}
            onExpandedRowsChange={setExpandedRowKeys}
            size="small"
          />
        </div>
      </Card>

      <Modal
        title={editingMenu && editingMenu.id ? '修改菜单' : '添加菜单'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingMenu(null);
        }}
        footer={null}
        width={800}
        destroyOnClose
      >
        <MenuForm
          key={editingMenu && editingMenu.id ? `edit-${editingMenu.id}` : `new-${formKey}`}
          menu={editingMenu}
          menuOptions={menuOptions}
          onSuccess={() => {
            setIsModalVisible(false);
            setEditingMenu(null);
            fetchMenuList();
          }}
          onCancel={() => {
            setIsModalVisible(false);
            setEditingMenu(null);
          }}
        />
      </Modal>
    </div>
  );
};

export default MenuList;
