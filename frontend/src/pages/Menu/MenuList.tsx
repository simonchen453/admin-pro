import React, { useState, useEffect, useRef } from 'react';
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
  Typography,
  Segmented,
  Tooltip,
  Dropdown,
  Badge,
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
  BookOutlined,
  ReloadOutlined,
  ColumnHeightOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from 'react-router-dom';
import {
  getMenuTreeListApi,
  deleteMenuApi,
  getMenuTreeSelectApi,
} from '../../api/menu';
import type {
  MenuEntity,
  MenuSearchForm,
  MenuTreeSelectNode,
} from '../../types';
import { MenuStatus, MenuType, MenuVisible } from '../../types';
import MenuForm from './MenuForm';
import '../../styles/crud.css';

const { Text } = Typography;

const MenuList: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [menuList, setMenuList] = useState<MenuEntity[]>([]);

  // 现代 ProTable 状态
  const [tableSize, setTableSize] = useState<'small' | 'middle' | 'large'>('middle');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [statusTab, setStatusTab] = useState<string>('ALL');
  const tableContainerRef = useRef<HTMLDivElement>(null);

  // 判断是否为图片路径
  const isImg = (icon: string): boolean => {
    return Boolean(
      icon &&
        (icon.endsWith('.png') ||
          icon.endsWith('.jpg') ||
          icon.endsWith('.jpeg') ||
          icon.endsWith('.gif'))
    );
  };

  // Ant Design 图标映射
  const iconComponents: Record<string, React.ReactNode> = {
    AppstoreOutlined: <AppstoreOutlined />,
    HomeOutlined: <HomeOutlined />,
    KeyOutlined: <KeyOutlined />,
    LogoutOutlined: <LogoutOutlined />,
    SettingOutlined: <SettingOutlined />,
    UserOutlined: <UserOutlined />,
    TeamOutlined: <TeamOutlined />,
    BarsOutlined: <BarsOutlined />,
    TagOutlined: <TagOutlined />,
    ToolOutlined: <ToolOutlined />,
    ApartmentOutlined: <ApartmentOutlined />,
    FileTextOutlined: <FileTextOutlined />,
    DesktopOutlined: <DesktopOutlined />,
    WifiOutlined: <WifiOutlined />,
    ClockCircleOutlined: <ClockCircleOutlined />,
    DatabaseOutlined: <DatabaseOutlined />,
    CodeOutlined: <CodeOutlined />,
    IdcardOutlined: <IdcardOutlined />,
    SlidersOutlined: <SlidersOutlined />,
    BookOutlined: <BookOutlined />,
  };

  const getIconComponent = (iconName: string) => {
    if (!iconName) return null;
    if (isImg(iconName)) {
      return null;
    }
    return iconComponents[iconName] || null;
  };

  const [searchForm, setSearchForm] = useState<MenuSearchForm>({});
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingMenu, setEditingMenu] = useState<MenuEntity | null>(null);
  const [menuOptions, setMenuOptions] = useState<MenuTreeSelectNode[]>([]);
  const [formKey, setFormKey] = useState(0);
  const [expandedRowKeys, setExpandedRowKeys] = useState<React.Key[]>([]);

  const handleTree = (
    data: MenuEntity[],
    id: string,
    parentId: string | number = 0
  ): MenuEntity[] => {
    const tree: MenuEntity[] = [];
    data.forEach((item) => {
      const itemParentId =
        typeof item.parentId === 'string' ? item.parentId : String(item.parentId);
      const compareParentId =
        typeof parentId === 'string' ? parentId : String(parentId);
      if (
        itemParentId === compareParentId ||
        (itemParentId === '0' && parentId === 0)
      ) {
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
      items.forEach((item) => {
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

  const fetchMenuTreeSelect = async () => {
    try {
      const response = await getMenuTreeSelectApi();
      if (response.restCode === '200') {
        const convertTreeSelectToMenuTreeSelectNode = (
          nodes: any[]
        ): MenuTreeSelectNode[] => {
          return nodes.map((node) => ({
            id: node.id || '',
            display: node.label || node.display || '',
            children: node.children
              ? convertTreeSelectToMenuTreeSelectNode(node.children)
              : undefined,
          }));
        };
        const menuList = response.data || [];
        const rootMenu: MenuTreeSelectNode = {
          id: '0',
          display: '主类目',
          children: convertTreeSelectToMenuTreeSelectNode(menuList),
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
    const query = {
      ...values,
      status: statusTab === 'ALL' ? values.status : statusTab,
    };
    setSearchForm(query);
    fetchMenuList(query);
  };

  const handleReset = () => {
    form.resetFields();
    setStatusTab('ALL');
    const emptyForm = {};
    setSearchForm(emptyForm);
    fetchMenuList(emptyForm);
  };

  const handleStatusTabChange = (val: string | number) => {
    const tab = String(val);
    setStatusTab(tab);
    const newStatus = tab === 'ALL' ? undefined : tab;
    const newForm = { ...searchForm, status: newStatus };
    setSearchForm(newForm);
    fetchMenuList(newForm);
  };

  const handleCreate = () => {
    setEditingMenu(null);
    setFormKey((prev) => prev + 1);
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
      orderNum: 0,
    };
    setEditingMenu(newMenu);
    setFormKey((prev) => prev + 1);
    fetchMenuTreeSelect();
    setIsModalVisible(true);
  };

  const handleEdit = (row: MenuEntity) => {
    if (!row.id) {
      message.error('菜单ID不存在');
      return;
    }
    setEditingMenu(row);
    setFormKey((prev) => prev + 1);
    fetchMenuTreeSelect();
    setIsModalVisible(true);
  };

  const handleDelete = (row: MenuEntity) => {
    if (!row.id) {
      message.error('菜单ID不存在');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除菜单「${row.display}」吗？如果有子菜单也会一并删除。`,
      okText: '确定删除',
      okType: 'danger',
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
      },
    });
  };

  const isAllExpanded = () => {
    const allKeys = getAllRowKeys(menuList);
    return (
      allKeys.length > 0 &&
      allKeys.length === expandedRowKeys.length &&
      allKeys.every((key) => expandedRowKeys.includes(key))
    );
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
      title: '菜单名称',
      dataIndex: 'display',
      key: 'display',
      width: 220,
      ellipsis: true,
      render: (display: string, record: MenuEntity) => {
        const iconComponent = getIconComponent(record.icon || '');
        return (
          <Space size={8}>
            {iconComponent ? (
              <span style={{ color: '#1677ff' }}>{iconComponent}</span>
            ) : isImg(record.icon || '') ? (
              <img
                src={record.icon}
                alt=""
                style={{ width: 16, height: 16, verticalAlign: 'middle' }}
              />
            ) : (
              <span style={{ width: 14, display: 'inline-block' }} />
            )}
            <span style={{ fontWeight: 600, color: 'var(--ap-text)' }}>
              {display}
            </span>
          </Space>
        );
      },
    },
    {
      title: '路由/标识',
      dataIndex: 'name',
      key: 'name',
      width: 180,
      ellipsis: true,
      render: (name: string) => (
        <span style={{ color: 'var(--ap-text-2)', fontFamily: 'monospace' }}>
          {name || '-'}
        </span>
      ),
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 90,
      align: 'center',
      render: (type: string) => {
        if (type === MenuType.DIRECTORY || type === 'M') {
          return <Tag color="purple">目录</Tag>;
        }
        if (type === MenuType.MENU || type === 'C') {
          return <Tag color="blue">菜单</Tag>;
        }
        return <Tag color="cyan">按钮</Tag>;
      },
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      key: 'orderNum',
      width: 70,
      align: 'center',
      render: (num: number) => <Tag>{num ?? 0}</Tag>,
    },
    {
      title: '权限字符',
      dataIndex: 'permission',
      key: 'permission',
      width: 180,
      ellipsis: true,
      render: (perm: string) =>
        perm ? (
          <Text code style={{ fontSize: 12 }}>
            {perm}
          </Text>
        ) : (
          <span style={{ color: 'var(--ap-text-3)' }}>-</span>
        ),
    },
    {
      title: '组件路径',
      dataIndex: 'url',
      key: 'url',
      width: 180,
      ellipsis: true,
      render: (url: string) =>
        url ? (
          <span
            style={{
              fontSize: 12,
              color: 'var(--ap-text-3)',
              fontFamily: 'monospace',
            }}
          >
            {url}
          </span>
        ) : (
          <span style={{ color: 'var(--ap-text-3)' }}>-</span>
        ),
    },
    {
      title: '显示',
      dataIndex: 'visible',
      key: 'visible',
      width: 90,
      align: 'center',
      render: (visible: string) =>
        visible === MenuVisible.SHOW || visible === '0' ? (
          <Badge status="success" text="显示" />
        ) : (
          <Badge status="default" text="隐藏" />
        ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 90,
      align: 'center',
      render: (status: string) =>
        status === MenuStatus.ACTIVE || status === '0' ? (
          <Badge status="success" text="正常" />
        ) : (
          <Badge status="error" text="停用" />
        ),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record: MenuEntity) => (
        <Space size={4}>
          <Button
            size="small"
            type="link"
            icon={<PlusOutlined />}
            onClick={() => handleAdd(record)}
          >
            新增子项
          </Button>
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
  ];

  useEffect(() => {
    fetchMenuList();
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
              { label: '全部状态', value: 'ALL' },
              { label: '正常启用', value: MenuStatus.ACTIVE },
              { label: '已停用', value: MenuStatus.INACTIVE },
            ]}
          />
        </div>

        <Form form={form} layout="vertical" onFinish={handleSearch}>
          <Row gutter={[16, 12]}>
            <Col xs={24} sm={12} md={8}>
              <Form.Item name="name" label="菜单名称 / 标识">
                <Input
                  placeholder="请输入菜单名称"
                  prefix={<SearchOutlined style={{ color: 'var(--ap-text-3)' }} />}
                  allowClear
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

      {/* 菜单树表格卡片 */}
      <div
        ref={tableContainerRef}
        className={`crud-table-card ${isFullscreen ? 'crud-fullscreen' : ''}`}
      >
        <div className="crud-toolbar">
          <div className="crud-toolbar-left">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新增菜单
            </Button>
            <Button
              icon={isAllExpanded() ? <CompressOutlined /> : <ExpandOutlined />}
              onClick={handleToggleExpand}
            >
              {isAllExpanded() ? '收起全部' : '展开全部'}
            </Button>
          </div>

          <div className="crud-toolbar-right">
            <Tooltip title="刷新数据">
              <Button
                type="text"
                className="crud-tool-btn"
                icon={<ReloadOutlined spin={loading} />}
                onClick={() => fetchMenuList()}
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

        <Table
          columns={columns}
          dataSource={menuList}
          loading={loading}
          rowKey={(record) => record.id || `menu-${record.name}`}
          pagination={false}
          expandedRowKeys={expandedRowKeys}
          onExpandedRowsChange={(keys) => setExpandedRowKeys(keys as React.Key[])}
          size={tableSize}
          locale={{
            emptyText: menuList.length === 0 && !loading ? '暂无匹配菜单' : undefined,
          }}
        />
      </div>

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
          key={
            editingMenu && editingMenu.id
              ? `edit-${editingMenu.id}`
              : `new-${formKey}`
          }
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
