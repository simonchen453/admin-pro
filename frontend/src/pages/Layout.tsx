import { Layout as AntLayout, Menu, Button, Dropdown, Tooltip, message, Breadcrumb, Badge, Avatar } from 'antd';
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
    TranslationOutlined,
    DownOutlined
} from '@ant-design/icons';
import { useAuthStore } from '../stores/useUserStore';
import { getMenuList } from '../api/menu';
import { getSystemInfoApi } from '../api/common';
import { getCurrentUserInfoApi } from '../api/auth';
import type { MenuItem, BackendMenuItem, SystemInfo, UserEntity, User } from '../types/index';
import { LogoMark } from '../components/Logo';
import './Layout.css';

const { Header, Sider, Content, Footer } = AntLayout;

function MainLayout() {
    const [collapsed, setCollapsed] = useState(false);
    const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
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

    const getIconComponent = (iconName: string) => {
        if (isImg(iconName)) {
            return null;
        }
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

            if (menu.icon) {
                if (isImg(menu.icon)) {
                    menuItem.icon = <img src={menu.icon} alt={menu.title} style={{ width: 16, height: 16 }} />;
                } else {
                    menuItem.icon = getIconComponent(menu.icon);
                }
            }

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

    // 获取层级父级Key
    const getParentKeys = (items: MenuItem[], targetKey: string, parents: string[] = []): string[] | null => {
        for (const item of items) {
            if (item.key === targetKey) {
                return parents;
            }
            if (item.children) {
                const found = getParentKeys(item.children, targetKey, [...parents, item.key]);
                if (found) return found;
            }
        }
        return null;
    };

    // 获取当前路径的面包屑数据
    const getBreadcrumbItems = () => {
        const currentPath = location.pathname;

        if (currentPath === '/' || currentPath === '/home') {
            return [{ title: '首页' }];
        }

        const items: { title: React.ReactNode, href?: string }[] = [
            { title: '首页', href: '/' }
        ];

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

        // 查找父级与当前项
        const matchedItem = findMenuItemByPath(menuItems, currentPath);
        if (matchedItem) {
            const parentKeys = getParentKeys(menuItems, matchedItem.key) || [];
            parentKeys.forEach(pKey => {
                const parent = menuItems.find(m => m.key === pKey);
                if (parent) {
                    items.push({ title: parent.label });
                }
            });
            items.push({
                title: matchedItem.label,
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
                const userInfo = (response as any)?.data || response;

                if (userInfo) {
                    const userEntity = userInfo as unknown as UserEntity;
                    setCurrentUserInfo(userEntity);

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

                    updateCurrentUser(user);
                }
            } catch (error) {
                console.error('获取用户信息失败:', error);
                if (currentUser) {
                    setCurrentUserInfo(currentUser as unknown as UserEntity);
                }
            }
        };
        loadCurrentUserInfo();
    }, []);

    // 加载菜单数据
    useEffect(() => {
        const loadMenus = async () => {
            try {
                setLoading(true);
                const backendMenus = await getMenuList();
                const convertedMenus = convertMenuItems(backendMenus);
                setMenuItems(convertedMenus);
            } catch (error) {
                console.error('加载菜单失败:', error);
                setMenuItems([]);
            } finally {
                setLoading(false);
            }
        };

        loadMenus();
    }, []);

    // 拍平所有叶子节点（防止多层嵌套，组内平铺）
    const flattenLeaves = (item: MenuItem): MenuItem[] => {
        if (!item.children || item.children.length === 0) return [item];
        return item.children.flatMap(flattenLeaves);
    };

    // 格式化为 AntD Menu items：静态分组导航（不收缩的分组样式）
    // 顶层有子项的节点渲染为不可收缩的分组标题（type: 'group'），组内成员平铺
    // 顶层叶子节点（如「首页」）作为独立项展示
    const buildGroupedMenuItems = (items: MenuItem[]): any[] => {
        return items.map(item => {
            if (!item.children || item.children.length === 0) {
                return {
                    key: item.key,
                    icon: item.icon,
                    label: item.label,
                };
            }
            const leaves = flattenLeaves(item);
            return {
                key: `grp-${item.key}`,
                type: 'group' as const,
                label: item.label,
                children: leaves.map(leaf => ({
                    key: leaf.key,
                    icon: leaf.icon,
                    label: leaf.label,
                })),
            };
        });
    };

    // 处理菜单点击
    const handleMenuClick = async ({ key }: { key: string }) => {
        if (key.startsWith('grp-')) return;

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
        if (menuItem && menuItem.path && menuItem.path !== '/logout') {
            navigate(menuItem.path);
        } else if (menuItem && menuItem.path === '/logout') {
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

    // 监听路由高亮菜单
    useEffect(() => {
        if (menuItems.length === 0) return;

        const currentPath = location.pathname;
        const matchedMenuItem = findMenuItemByPath(menuItems, currentPath);

        if (matchedMenuItem) {
            setSelectedKeys([matchedMenuItem.key]);
        } else {
            setSelectedKeys([]);
        }
    }, [location.pathname, menuItems]);

    const displayName =
        currentUserInfo?.realName ||
        currentUser?.realName ||
        currentUser?.name ||
        currentUserInfo?.loginName ||
        '管理员';
    const roleName = currentUserInfo?.roleName || '系统管理员';
    const avatarUrl = currentUserInfo?.avatarUrl || currentUser?.avatarUrl || currentUser?.avatar;

    const accountMenu = {
        items: [
            {
                key: 'settings',
                label: '个人设置',
                icon: <SettingOutlined />,
                onClick: () => navigate('/settings')
            },
            {
                type: 'divider' as const,
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
            {/* 纯净浅色侧栏导航 */}
            <Sider
                collapsible
                collapsed={collapsed}
                onCollapse={(value) => setCollapsed(value)}
                trigger={null}
                breakpoint="lg"
                collapsedWidth={64}
                width={240}
                className="ap-rail-sider"
                style={{ position: 'fixed', left: 0, top: 0, bottom: 0, zIndex: 100 }}
            >
                <div className="ap-brand">
                    <span className="ap-logo"><LogoMark size={16} /></span>
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
                        onClick={handleMenuClick}
                        items={buildGroupedMenuItems(menuItems)}
                        style={{ background: 'transparent', borderRight: 0 }}
                    />
                </div>

                <Dropdown menu={accountMenu} placement="topRight" trigger={['click']}>
                    <div className="ap-rail-user">
                        {avatarUrl ? (
                            <img className="ap-ru-av" src={avatarUrl} alt="" />
                        ) : (
                            <span className="ap-ru-av">{displayName.slice(0, 1)}</span>
                        )}
                        {!collapsed && (
                            <div className="ap-rail-user-inner">
                                <div className="ap-ru-n">{displayName}</div>
                                <div className="ap-ru-r">{roleName}</div>
                            </div>
                        )}
                    </div>
                </Dropdown>
            </Sider>

            <AntLayout style={{
                marginLeft: collapsed ? 64 : 240,
                transition: 'margin-left 0.2s cubic-bezier(0.2, 0, 0, 1)',
                display: 'flex',
                flexDirection: 'column',
                minHeight: '100vh',
                flex: 1
            }}>
                <Header className="ap-topbar" style={{ position: 'sticky', top: 0, zIndex: 99 }}>
                    <div className="ap-topbar-l">
                        <Button
                            type="text"
                            shape="circle"
                            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                            onClick={() => setCollapsed(!collapsed)}
                            aria-label={collapsed ? '展开侧栏' : '收起侧栏'}
                            style={{ marginLeft: -8, marginRight: 4 }}
                        />
                        <Breadcrumb items={getBreadcrumbItems()} />
                    </div>

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

                        <Dropdown menu={accountMenu} placement="bottomRight" arrow>
                            <div className="ap-topbar-user">
                                <Avatar size={28} src={avatarUrl} style={{ backgroundColor: '#1677ff' }}>
                                    {displayName.slice(0, 1)}
                                </Avatar>
                                <span className="ap-topbar-name">{displayName}</span>
                                <DownOutlined style={{ fontSize: 10, color: 'var(--ap-text-3)' }} />
                            </div>
                        </Dropdown>
                    </div>
                </Header>

                <Content style={{ flex: '1 1 auto', display: 'flex', flexDirection: 'column' }}>
                    <div className="ap-page fade-in">
                        <Outlet />
                    </div>
                </Content>

                <Footer className="ap-foot">
                    {systemInfo?.copyRight || `Copyright © ${new Date().getFullYear()} AdminPro. All Rights Reserved.`}
                </Footer>
            </AntLayout>
        </AntLayout>
    );
}

export default MainLayout;