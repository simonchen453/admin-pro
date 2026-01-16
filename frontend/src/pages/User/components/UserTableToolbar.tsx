import React from 'react';
import { Button, Space, Dropdown, Upload, message } from 'antd';
import type { MenuProps, UploadProps } from 'antd';
import {
    PlusOutlined,
    DeleteOutlined,
    UploadOutlined,
    DownloadOutlined,
    ReloadOutlined
} from '@ant-design/icons';

/**
 * 用户列表工具栏组件属性
 */
export interface UserTableToolbarProps {
    /** 选中的用户数量 */
    selectedCount: number;
    /** 是否正在加载 */
    loading?: boolean;
    /** 是否正在导入 */
    importLoading?: boolean;
    /** 新增按钮点击 */
    onAdd: () => void;
    /** 批量删除按钮点击 */
    onBatchDelete: () => void;
    /** 刷新按钮点击 */
    onRefresh: () => void;
    /** 导入成功回调 */
    onImportSuccess: () => void;
    /** 导出选中用户 */
    onExportSelected: () => void;
    /** 导出全部用户 */
    onExportAll: () => void;
    /** 导入文件上传处理 */
    onImportUpload: (file: File) => void;
}

/**
 * 用户列表工具栏组件
 * 
 * 包含新增、批量删除、导入、导出、刷新等操作按钮
 */
const UserTableToolbar: React.FC<UserTableToolbarProps> = ({
    selectedCount,
    loading = false,
    importLoading = false,
    onAdd,
    onBatchDelete,
    onRefresh,
    onImportUpload,
    onExportSelected,
    onExportAll
}) => {
    // 导出菜单
    const exportMenuItems: MenuProps['items'] = [
        {
            key: 'selected',
            label: '导出选中',
            icon: <DownloadOutlined />,
            disabled: selectedCount === 0,
            onClick: onExportSelected
        },
        {
            key: 'all',
            label: '导出全部',
            icon: <DownloadOutlined />,
            onClick: onExportAll
        }
    ];

    // 导入上传配置
    const uploadProps: UploadProps = {
        accept: '.xlsx,.xls',
        showUploadList: false,
        beforeUpload: (file) => {
            onImportUpload(file);
            return false;
        }
    };

    return (
        <Space wrap style={{ marginBottom: 16 }}>
            <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={onAdd}
            >
                新增
            </Button>

            <Button
                danger
                icon={<DeleteOutlined />}
                disabled={selectedCount === 0}
                onClick={onBatchDelete}
            >
                批量删除 {selectedCount > 0 && `(${selectedCount})`}
            </Button>

            <Upload {...uploadProps}>
                <Button
                    icon={<UploadOutlined />}
                    loading={importLoading}
                >
                    导入
                </Button>
            </Upload>

            <Dropdown menu={{ items: exportMenuItems }}>
                <Button icon={<DownloadOutlined />}>
                    导出
                </Button>
            </Dropdown>

            <Button
                icon={<ReloadOutlined />}
                onClick={onRefresh}
                loading={loading}
            >
                刷新
            </Button>
        </Space>
    );
};

export default UserTableToolbar;
