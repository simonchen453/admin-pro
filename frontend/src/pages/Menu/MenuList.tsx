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
  Typography
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
  ClearOutlined,
  HomeOutlined,
  ExpandOutlined,
  CompressOutlined,
  UserOutlined,
  SettingOutlined,
  KeyOutlined,
  LogoutOutlined,
  TeamOutlined,
  BarsOutlined,
  TagOutlined,
  ToolOutlined,
  ApartmentOutlined,
  FileTextOutlined,
  DesktopOutlined,
  WifiOutlined,
  ClockCircleOutlined,
  DatabaseOutlined,
  CodeOutlined,
  AppstoreOutlined,
  IdcardOutlined,
  SlidersOutlined,
  BookOutlined
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
const { Title } = Typography;

const MenuList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [menuList, setMenuList] = useState<MenuEntity[]>([]);

  // 判断是否为图片路径
  const isImg = (icon: string): boolean => {
    return Boolean(icon && (icon.endsWith('.png') || icon.endsWith('.jpg') || icon.endsWith('.jpeg') || icon.endsWith('.gif')));
  };

  // Ant Design图标映射
  const iconComponents: Record<string, React.ReactNode> = {
    'AppstoreOutlined': <AppstoreOutlined />,
    'HomeOutlined': <HomeOutlined />,
    'KeyOutlined': <KeyOutlined />,
    'LogoutOutlined': <LogoutOutlined />,
    'SettingOutlined': <SettingOutlined />,
    'UserOutlined': <UserOutlined />,
    'TeamOutlined': <TeamOutlined />,
    'BarsOutlined': <BarsOutlined />,
    'TagOutlined': <TagOutlined />,
    'ToolOutlined': <ToolOutlined />,
    'ApartmentOutlined': <ApartmentOutlined />,
    'FileTextOutlined': <FileTextOutlined />,
    'DesktopOutlined': <DesktopOutlined />,
    'WifiOutlined': <WifiOutlined />,
    'ClockCircleOutlined': <ClockCircleOutlined />,
    'DatabaseOutlined': <DatabaseOutlined />,
    'CodeOutlined': <CodeOutlined />,
    'IdcardOutlined': <IdcardOutlined />,
    'SlidersOutlined': <SlidersOutlined />,
    'BookOutlined': <BookOutlined />,
  };

  // 根据图标名称获取Ant Design图标组件
  const getIconComponent = (iconName: string) => {
    if (!iconName) return null;
    // 如果是图片路径，返回null，后续会处理
    if (isImg(iconName)) {
      return null;
    }
    // 直接使用Ant Design图标名称
    return iconComponents[iconName] || null;
  };
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
        // /menus/tree 返回 TreeSelect 格式: { id, label, children }
        // 需要转换为 MenuTreeSelectNode 格式: { id, display, children }
        const convertTreeSelectToMenuTreeSelectNode = (nodes: any[]): MenuTreeSelectNode[] => {
          return nodes.map(node => ({
            id: node.id || '',
            display: node.label || node.display || '',
            children: node.children ? convertTreeSelectToMenuTreeSelectNode(node.children) : undefined
          }));
        };
        const menuList = response.data || [];
        const rootMenu: MenuTreeSelectNode = {
          id: '0',
          display: '主类目',
          children: convertTreeSelectToMenuTreeSelectNode(menuList)
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
      render: (icon: string) => {
        if (!icon) return '-';
        // 如果是图片路径，显示图片
        if (isImg(icon)) {
          return <img src={icon} alt="icon" style={{ width: 16, height: 16 }} />;
        }
        // 尝试获取Ant Design图标组件
        const iconComponent = getIconComponent(icon);
        if (iconComponent) {
          return iconComponent;
        }
        // 如果都不匹配，显示原始文本（可能是旧的FontAwesome格式或其他格式）
        return <span title={icon}>{icon}</span>;
      }
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      key: 'orderNum',
      width: 60,
      align: 'center'
    },
    {
      title: '菜单类型',
      dataIndex: 'type',
      key: 'type',
      width: 80,
      align: 'center',
      render: (type: string) => {
        const typeMap: Record<string, { color: string; text: string }> = {
          [MenuType.DIRECTORY]: { color: 'blue', text: '目录' },
          [MenuType.MENU]: { color: 'green', text: '菜单' },
          [MenuType.BUTTON]: { color: 'orange', text: '按钮' }
        };
        const info = typeMap[type] || { color: 'default', text: type };
        return <Tag color={info.color}>{info.text}</Tag>;
      }
    },
    {
      title: '显示状态',
      dataIndex: 'visible',
      key: 'visible',
      width: 80,
      align: 'center',
      render: (visible: string) => {
        const visibleMap: Record<string, { color: string; text: string }> = {
          [MenuVisible.SHOW]: { color: 'cyan', text: '显示' },
          [MenuVisible.HIDE]: { color: 'geekblue', text: '隐藏' }
        };
        const info = visibleMap[visible] || { color: 'default', text: visible };
        return <Tag color={info.color}>{info.text}</Tag>;
      }
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
      width: 180,
      render: (date: string) => formatDateTime(date)
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      render: (_, record: MenuEntity) => (
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
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record)}
          >
            新增
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
      )
    }
  ];

  useEffect(() => {
    fetchMenuList();
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
            <Col xs={24} sm={12} md={6}>
              <Form.Item name="name" style={{ marginBottom: 0 }}>
                <Input
                  placeholder="菜单名称"
                  prefix={<SearchOutlined style={{ color: '#cbd5e1' }} />}
                  allowClear
                  size="large"
                  onPressEnter={() => form.submit()}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Form.Item name="status" style={{ marginBottom: 0 }}>
                <Select placeholder="状态" allowClear style={{ width: '100%' }} size="large">
                  <Option value={MenuStatus.ACTIVE}>正常</Option>
                  <Option value={MenuStatus.INACTIVE}>停用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={6}>
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
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增菜单
            </Button>
            <Button
              icon={isAllExpanded() ? <CompressOutlined /> : <ExpandOutlined />}
              onClick={handleToggleExpand}
            >
              {isAllExpanded() ? '收起全部' : '展开全部'}
            </Button>
          </Space>
        </div>

        <div className="modern-table">
          <Table
            columns={columns}
            dataSource={menuList}
            loading={loading}
            rowKey={(record) => record.id || `menu-${record.name}`}
            pagination={false}
            expandedRowKeys={expandedRowKeys}
            onExpandedRowsChange={setExpandedRowKeys}
            size="middle"
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
        destroyOnHidden
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
