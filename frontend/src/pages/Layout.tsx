import { Layout as AntLayout, Menu, Button, Dropdown, Tooltip, message } from 'antd';
import { useState, useEffect } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
    UserOutlined,
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
    AppstoreOutlined,
    IdcardOutlined,
    SlidersOutlined,
    BookOutlined,
    BellOutlined,
    TranslationOutlined
} from '@ant-design/icons';
import { Breadcrumb, Badge } from 'antd';
import { useAuthStore } from '../stores/useUserStore';
import { getMenuList } from '../api/menu';
import { getSystemInfoApi } from '../api/common';
import { getCurrentUserInfoApi } from '../api/auth';
import type { MenuItem, BackendMenuItem, SystemInfo, UserEntity, User } from '../types/index';
import { LogoMark, ChevronRight } from '../components/Logo';
import './Layout.css';

const { Header, Sider, Content, Footer } = AntLayout;

function MainLayout() {
    const [collapsed, setCollapsed] = useState(false);
    const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
    const [openKeys, setOpenKeys] = useState<string[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
    const [, setLoading] = useState(true);
    const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);
    const [currentUserInfo, setCurrentUserInfo] = useState<UserEntity | null>(null);
    const navigate = useNavigate();
    const { logout, currentUser, updateCurrentUser } = useAuthStore();

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
        // 如果是图片路径，返回null，后续会处理
        if (isImg(iconName)) {
            return null;
        }
        // 直接使用Ant Design图标名称
        return iconComponents[iconName] || <UserOutlined />;
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

    // 获取当前路径的面包屑数据
    const getBreadcrumbItems = () => {
        const currentPath = location.pathname;

        // 如果是首页，直接返回首页面包屑，避免重复
        if (currentPath === '/' || currentPath === '/home') {
            return [{ title: '首页' }];
        }

        const items: { title: React.ReactNode, href?: string }[] = [
            { title: '首页', href: '/' }
        ];

        // 特殊路径处理（不在菜单中的页面）
        const specialPaths: Record<string, string> = {
            '/settings': '个人设置'
        };

        if (specialPaths[currentPath]) {
            items.push({
                title: specialPaths[currentPath],
                href: undefined
            });
            return items;
        }

        // 查找当前页面对应的菜单项
        let currentMenuItem: MenuItem | null = null;

        const findItem = (items: MenuItem[]) => {
            for (const item of items) {
                if (item.path === currentPath) {
                    currentMenuItem = item;
                    return;
                }
                if (item.children) {
                    findItem(item.children);
                }
            }
        };

        findItem(menuItems);

        if (currentMenuItem) {
            items.push({
                title: (currentMenuItem as MenuItem).label,
                // 最后一级不加链接
                href: undefined
            });
        }

        return items;
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

    // 加载当前用户详细信息
    useEffect(() => {
        const loadCurrentUserInfo = async () => {
            try {
                const response = await getCurrentUserInfoApi();

                // getCurrentUserInfoApi 返回的是 data 字段，即 UserEntity
                // 但根据响应拦截器，实际返回的可能是整个响应对象
                const userInfo = (response as any)?.data || response;

                if (userInfo) {
                    const userEntity = userInfo as unknown as UserEntity;
                    setCurrentUserInfo(userEntity);

                    // Convert UserEntity to User for store
                    const user: User = {
                        id: userEntity.id,
                        loginName: userEntity.loginName,
                        realName: userEntity.realName,
                        name: userEntity.realName || userEntity.loginName,
                        avatarUrl: userEntity.avatarUrl,
                        mobileNo: userEntity.mobileNo,
                        email: userEntity.email,
                        userDomain: userEntity.userDomain,

                        status: (userEntity.status === 'active' || userEntity.status === '1') ? 'active' : 'inactive'
                    };

                    // Sync to store
                    updateCurrentUser(user);
                }
            } catch (error) {
                console.error('获取用户信息失败:', error);
                // 如果获取失败，尝试使用 currentUser 中的数据
                if (currentUser) {

                    setCurrentUserInfo(currentUser as unknown as UserEntity);
                }
            }
        };
        // 无论currentUser是否存在，都尝试加载用户信息（因为可能通过session认证）
        loadCurrentUserInfo();
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


                }
                setMenuItems(convertedMenus);
            } catch (error) {
                console.error('加载菜单失败:', error);
                // 如果加载失败，清空菜单
                setMenuItems([]);
            } finally {
                setLoading(false);
            }
        };

        loadMenus();
    }, []);

    // 处理菜单点击
    const handleMenuClick = async ({ key }: { key: string }) => {
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
            try {
                await logout();
                setMenuItems([]);
                navigate('/login', { replace: true });
            } catch (error) {
                console.error('登出失败:', error);
                setMenuItems([]);
                navigate('/login', { replace: true });
            }
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

    const displayName =
        currentUserInfo?.realName ||
        currentUser?.realName ||
        currentUser?.name ||
        currentUserInfo?.loginName ||
        '管理员';
    const roleName = currentUserInfo?.roleName || '系统管理员';
    const avatarUrl = currentUserInfo?.avatarUrl || currentUser?.avatarUrl || currentUser?.avatar;

    // 账号菜单从顶栏挪到了侧栏底部：这类操作一天用不了两次，
    // 不该常年占着顶栏右上角最显眼的位置。
    const accountMenu = {
        items: [
            {
                key: 'settings',
                label: '个人设置',
                icon: <SettingOutlined />,
                onClick: () => navigate('/settings')
            },
            {
                key: 'logout',
                label: '退出登录',
                icon: <LogoutOutlined />,
                danger: true,
                onClick: async () => {
                    try {
                        await logout();
                    } catch (error) {
                        console.error('登出失败:', error);
                    } finally {
                        setMenuItems([]);
                        navigate('/login', { replace: true });
                    }
                }
            }
        ]
    };

    return (
        <AntLayout style={{ minHeight: '100vh', display: 'flex', flexDirection: 'row' }}>
            {/* 侧栏和工作区一样是纯白，只靠右侧一条发丝线分开 —— 没有深色玻璃、没有投影 */}
            <Sider
                collapsible
                collapsed={collapsed}
                onCollapse={(value) => setCollapsed(value)}
                trigger={null}
                breakpoint="lg"
                collapsedWidth="0"
                width={248}
                className="ap-rail-sider"
                style={{ position: 'fixed', left: 0, top: 0, bottom: 0, zIndex: 1001 }}
            >
                <div className="ap-brand">
                    <span className="ap-logo"><LogoMark /></span>
                    {!collapsed && (
                        <span className="ap-brand-t">
                            {systemInfo?.platformShortName || 'AdminPro'}
                        </span>
                    )}
                </div>

                <div className="ap-rail-nav custom-scrollbar">
                    <Menu
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
                                children: child.children?.map(subChild => ({
                                    key: subChild.key,
                                    icon: subChild.icon,
                                    label: subChild.label,
                                })),
                            })),
                        }))}
                        style={{ background: 'transparent', borderRight: 0 }}
                    />
                </div>

                <Dropdown menu={accountMenu} placement="topRight" trigger={['click']}>
                    <div className="ap-rail-user">
                        {avatarUrl
                            ? <img className="ap-ru-av" src={avatarUrl} alt="" />
                            : <span className="ap-ru-av">{displayName.slice(0, 1)}</span>}
                        <span style={{ minWidth: 0 }}>
                            <span className="ap-ru-n">{displayName}</span><br />
                            <span className="ap-ru-r">{roleName}</span>
                        </span>
                        <span className="ap-ru-x"><ChevronRight /></span>
                    </div>
                </Dropdown>
            </Sider>

            <AntLayout style={{
                marginLeft: collapsed ? 0 : 248,
                transition: 'margin-left 0.2s',
                display: 'flex',
                flexDirection: 'column',
                minHeight: '100vh',
                flex: 1
            }}>
                <Header className="ap-topbar" style={{ position: 'sticky', top: 0, zIndex: 1000 }}>
                    <Button
                        type="text"
                        shape="circle"
                        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                        onClick={() => setCollapsed(!collapsed)}
                        aria-label={collapsed ? '展开侧栏' : '收起侧栏'}
                        style={{ marginLeft: -8, marginRight: 4 }}
                    />
                    <Breadcrumb items={getBreadcrumbItems()} />

                    <div className="ap-topbar-r">
                        <Dropdown
                            menu={{
                                items: [
                                    { key: 'zh-CN', label: '简体中文', onClick: () => message.success('已切换至简体中文') },
                                    { key: 'en-US', label: 'English', onClick: () => message.success('Switched to English') }
                                ]
                            }}
                            placement="bottomRight"
                        >
                            <Button type="text" shape="circle" icon={<TranslationOutlined />} aria-label="切换语言" />
                        </Dropdown>
                        <Tooltip title="消息通知">
                            <Button
                                type="text"
                                shape="circle"
                                aria-label="消息通知"
                                icon={
                                    <Badge dot offset={[-2, 2]}>
                                        <BellOutlined />
                                    </Badge>
                                }
                            />
                        </Tooltip>
                    </div>
                </Header>

                {/* 留白由 .ap-page 统一给：32 的内边距、28 的纵向间距，页面自己不再加 margin */}
                <Content style={{ flex: '1 1 auto', display: 'flex', flexDirection: 'column' }}>
                    <div className="ap-page fade-in">
                        <Outlet />
                    </div>
                </Content>

                <Footer className="ap-foot">
                    {systemInfo?.copyRight || `Copyright © ${new Date().getFullYear()} AdminPro`}
                </Footer>
            </AntLayout>
        </AntLayout>
    );
}

export default MainLayout;