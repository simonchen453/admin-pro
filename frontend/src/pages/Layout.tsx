import { Layout as AntLayout, Menu, theme, Button, Avatar, Dropdown, Space, Typography } from 'antd';
import { useState, useEffect } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { 
    UserOutlined, 
    DashboardOutlined, 
    MenuFoldOutlined, 
    MenuUnfoldOutlined, 
    LogoutOutlined, 
    SettingOutlined,
    HomeOutlined,
    KeyOutlined,
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
    AppstoreOutlined
} from '@ant-design/icons';
import { useAuthStore } from '../stores/useUserStore';
import { getMenuList } from '../api/menu';
import { getSystemInfoApi } from '../api/common';
import type { MenuItem, BackendMenuItem, SystemInfo } from '../types/index';
import './Layout.css';

const { Header, Sider, Content, Footer } = AntLayout;
const { Text } = Typography;

function MainLayout() {
    const [collapsed, setCollapsed] = useState(false);
    const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
    const [openKeys, setOpenKeys] = useState<string[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
    const [, setLoading] = useState(true);
    const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);
    const navigate = useNavigate();
    const { logout, currentUser } = useAuthStore();
    const {
        token: { colorBgContainer },
    } = theme.useToken();

    const location = useLocation();

    // 判断是否为图片
    const isImg = (icon: string): boolean => {
        return Boolean(icon && (icon.endsWith('.png') || icon.endsWith('.jpg') || icon.endsWith('.jpeg') || icon.endsWith('.gif')));
    };

    // 清理URL，移除null和_m_id参数
    const cleanUrl = (url: string): string | null => {
        if (!url || url === 'null' || url.startsWith('null?')) {
            return null;
        }
        // 移除_m_id参数
        return url.split('?')[0];
    };

    // FontAwesome图标映射到Antd图标
    const getIconComponent = (iconClass: string) => {
        const iconMap: Record<string, React.ReactNode> = {
            'fa fa-clone': <AppstoreOutlined />,
            'fa fa-home': <HomeOutlined />,
            'fa fa-key': <KeyOutlined />,
            'fa fa-outdent': <LogoutOutlined />,
            'fa fa-cog': <SettingOutlined />,
            'fa fa-user': <UserOutlined />,
            'fa fa-users': <TeamOutlined />,
            'fa fa-bars': <BarsOutlined />,
            'fa fa-tag': <TagOutlined />,
            'fa fa-wrench': <ToolOutlined />,
            'fa fa-sitemap': <ApartmentOutlined />,
            'fa fa-clipboard': <FileTextOutlined />,
            'fa fa-list-alt': <FileTextOutlined />,
            'fa fa-desktop': <DesktopOutlined />,
            'fa fa-wifi': <WifiOutlined />,
            'fa fa-tasks': <ClockCircleOutlined />,
            'fa fa-server': <DatabaseOutlined />,
            'fa fa-code': <CodeOutlined />,
            'fa fa-cube': <AppstoreOutlined />,
        };
        return iconMap[iconClass] || <UserOutlined />;
    };

    // 转换后端菜单格式为Antd菜单格式
    const convertMenuItems = (backendMenus: BackendMenuItem[]): MenuItem[] => {
        return backendMenus.map(menu => {
            const cleanedUrl = cleanUrl(menu.url);
            const menuItem: MenuItem = {
                key: menu.index,
                label: menu.title,
                path: cleanedUrl || undefined,
            };

            // 处理图标
            if (menu.icon) {
                if (isImg(menu.icon)) {
                    menuItem.icon = <img src={menu.icon} alt={menu.title} style={{ width: 16, height: 16 }} />;
                } else {
                    menuItem.icon = getIconComponent(menu.icon);
                }
            }

            // 处理子菜单
            if (menu.subs && menu.subs.length > 0) {
                menuItem.children = convertMenuItems(menu.subs);
            }

            return menuItem;
        });
    };

    // 根据路径查找菜单项
    const findMenuItemByPath = (items: MenuItem[], path: string): MenuItem | null => {
        for (const item of items) {
            if (item.path === path) {
                return item;
            }
            if (item.children) {
                const found = findMenuItemByPath(item.children, path);
                if (found) return found;
            }
        }
        return null;
    };

    // 获取菜单项的所有父级key
    const getParentKeys = (items: MenuItem[], targetKey: string, parentKeys: string[] = []): string[] | null => {
        for (const item of items) {
            if (item.key === targetKey) {
                return parentKeys;
            }
            if (item.children) {
                const found = getParentKeys(item.children, targetKey, [...parentKeys, item.key]);
                if (found !== null) {
                    return found;
                }
            }
        }
        return null;
    };

    // 加载系统信息
    useEffect(() => {
        const loadSystemInfo = async () => {
            try {
                const response = await getSystemInfoApi();
                if (response.data) {
                    setSystemInfo(response.data);
                }
            } catch (error) {
                console.error('获取系统信息失败:', error);
            }
        };
        loadSystemInfo();
    }, []);

    // 加载菜单数据
    useEffect(() => {
        const loadMenus = async () => {
            try {
                setLoading(true);
                const backendMenus = await getMenuList();
                const convertedMenus = convertMenuItems(backendMenus);
                // 开发环境下打印菜单数据
                if (import.meta.env.DEV) {
                    console.log('原始菜单数据:', backendMenus);
                    console.log('转换后菜单数据:', convertedMenus);
                }
                setMenuItems(convertedMenus);
            } catch (error) {
                console.error('加载菜单失败:', error);
                // 如果加载失败，使用默认菜单
                setMenuItems([
                    {
                        key: '/',
                        icon: <DashboardOutlined />,
                        label: '仪表盘',
                        path: '/',
                    },
                    {
                        key: 'user-management',
                        icon: <TeamOutlined />,
                        label: '用户管理',
                        children: [
                            {
                                key: '/user/list',
                                icon: <UserOutlined />,
                                label: '用户列表',
                                path: '/user/list',
                            },
                            {
                                key: '/admin/user',
                                icon: <UserOutlined />,
                                label: '用户管理',
                                path: '/admin/user',
                            },
                        ],
                    },
                    {
                        key: 'role-management',
                        icon: <TagOutlined />,
                        label: '角色管理',
                        children: [
                            {
                                key: '/roles',
                                icon: <TagOutlined />,
                                label: '角色列表',
                                path: '/roles',
                            },
                            {
                                key: '/admin/role',
                                icon: <TagOutlined />,
                                label: '角色管理',
                                path: '/admin/role',
                            },
                            {
                                key: '/role/list',
                                icon: <TagOutlined />,
                                label: '角色列表',
                                path: '/role/list',
                            },
                        ],
                    },
                ]);
            } finally {
                setLoading(false);
            }
        };

        loadMenus();
    }, []);

    const userMenuItems = [
        {
            key: 'profile',
            icon: <UserOutlined />,
            label: '个人资料',
        },
        {
            key: 'settings',
            icon: <SettingOutlined />,
            label: '设置',
        },
        {
            type: 'divider' as const,
        },
        {
            key: 'logout',
            icon: <LogoutOutlined />,
            label: '退出登录',
        },
    ];

    const handleUserMenuClick = ({ key }: { key: string }) => {
        switch (key) {
            case 'logout':
                logout();
                navigate('/login', { replace: true });
                break;
            default:
                console.log('点击了:', key);
        }
    };

    // 处理菜单点击
    const handleMenuClick = ({ key }: { key: string }) => {
        const findMenuItem = (items: MenuItem[], targetKey: string): MenuItem | null => {
            for (const item of items) {
                if (item.key === targetKey) {
                    return item;
                }
                if (item.children) {
                    const found = findMenuItem(item.children, targetKey);
                    if (found) return found;
                }
            }
            return null;
        };

        const menuItem = findMenuItem(menuItems, key);
        // 只有当菜单项有有效路径时才进行路由跳转
        if (menuItem && menuItem.path && menuItem.path !== '/logout') {
            navigate(menuItem.path);
        } else if (menuItem && menuItem.path === '/logout') {
            // 特殊处理退出登录
            logout();
            navigate('/login', { replace: true });
        }
    };

    // 处理菜单展开/收起
    const handleOpenChange = (keys: string[]) => {
        setOpenKeys(keys);
    };

    // 根据当前路由自动展开菜单并高亮
    useEffect(() => {
        if (menuItems.length === 0) return;

        const currentPath = location.pathname;
        const matchedMenuItem = findMenuItemByPath(menuItems, currentPath);

        if (matchedMenuItem) {
            setSelectedKeys([matchedMenuItem.key]);
            const parentKeys = getParentKeys(menuItems, matchedMenuItem.key);
            if (parentKeys && parentKeys.length > 0 && !collapsed) {
                setOpenKeys(parentKeys);
            }
        } else {
            setSelectedKeys([]);
        }
    }, [location.pathname, menuItems, collapsed]);

    // 当侧边栏折叠时，清空展开的菜单
    useEffect(() => {
        if (collapsed) {
            setOpenKeys([]);
        } else {
            // 侧边栏展开时，根据当前路由重新展开菜单
            if (menuItems.length > 0) {
                const currentPath = location.pathname;
                const matchedMenuItem = findMenuItemByPath(menuItems, currentPath);
                if (matchedMenuItem) {
                    const parentKeys = getParentKeys(menuItems, matchedMenuItem.key);
                    if (parentKeys && parentKeys.length > 0) {
                        setOpenKeys(parentKeys);
                    }
                }
            }
        }
    }, [collapsed, menuItems, location.pathname]);

    return (
        <AntLayout style={{ minHeight: '100vh' }}>
            <Sider 
                collapsible 
                collapsed={collapsed} 
                onCollapse={(value) => setCollapsed(value)}
                trigger={null}
                breakpoint="lg"
                collapsedWidth="0"
                width={280}
            >
                <div className="logo">
                    <div className="layout-logo-background">
                        <img src="/logo.svg" alt="Admin Pro" className="layout-logo-image" />
                    </div>
                    {!collapsed && (
                        <div className="logo-text">
                            <div className="logo-title">{systemInfo?.platformShortName || 'Admin Pro'}</div>
                            <div className="logo-subtitle">管理系统</div>
                        </div>
                    )}
                </div>
                <Menu
                    theme="dark"
                    mode="inline"
                    selectedKeys={selectedKeys}
                    openKeys={openKeys}
                    onClick={handleMenuClick}
                    onOpenChange={handleOpenChange}
                    items={menuItems.map(item => ({
                        key: item.key,
                        icon: item.icon,
                        label: item.label,
                        children: item.children?.map(child => ({
                            key: child.key,
                            icon: child.icon,
                            label: child.label,
                        })),
                    }))}
                />
            </Sider>
            <AntLayout>
                <Header style={{ 
                    padding: '0 16px', 
                    background: colorBgContainer,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    boxShadow: '0 1px 4px rgba(0,21,41,.08)'
                }}>
                    <Button
                        type="text"
                        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                        onClick={() => setCollapsed(!collapsed)}
                        style={{ fontSize: '16px', width: 64, height: 64 }}
                    />
                    
                    <Space>
                        <Dropdown
                            menu={{
                                items: userMenuItems,
                                onClick: handleUserMenuClick,
                            }}
                            placement="bottomRight"
                        >
                            <Space style={{ cursor: 'pointer' }}>
                                <Avatar icon={<UserOutlined />} />
                                <Text>{currentUser?.name || '管理员'}</Text>
                            </Space>
                        </Dropdown>
                    </Space>
                </Header>
                <Content style={{ 
                    margin: '16px', 
                    padding: '24px', 
                    background: colorBgContainer, 
                    minHeight: 360,
                    borderRadius: '8px',
                    boxShadow: '0 1px 2px rgba(0,0,0,0.03)'
                }}>
                    <Outlet />
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                    <div className="copyright-text">
                        {systemInfo?.copyRight || `Copyright © ${new Date().getFullYear()} Admin Pro 管理系统. All rights reserved.`}
                    </div>
                    {(systemInfo?.releaseVersion || systemInfo?.buildVersion) && (
                        <div className="copyright-subtitle" style={{ marginTop: '8px' }}>
                            {systemInfo.releaseVersion && <span>版本: {systemInfo.releaseVersion}</span>}
                            {systemInfo.releaseVersion && systemInfo.buildVersion && <span style={{ margin: '0 8px' }}>|</span>}
                            {systemInfo.buildVersion && <span>构建版本: {systemInfo.buildVersion}</span>}
                        </div>
                    )}
                </Footer>
            </AntLayout>
        </AntLayout>
    );
}

export default MainLayout;