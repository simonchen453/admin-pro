import { useState, useCallback } from 'react';
import { Modal, message } from 'antd';

/**
 * 模态框配置接口
 */
export interface ModalConfig {
    /** 标题 */
    title?: string;
    /** 确认文本 */
    okText?: string;
    /** 取消文本 */
    cancelText?: string;
}

/**
 * 模态框 Hook 返回值接口
 */
export interface UseModalReturn<T = unknown> {
    /** 是否可见 */
    visible: boolean;
    /** 当前编辑的数据 */
    editingData: T | null;
    /** 是否为编辑模式 */
    isEdit: boolean;
    /** 是否正在提交 */
    loading: boolean;
    /** 打开新建模态框 */
    openCreate: () => void;
    /** 打开编辑模态框 */
    openEdit: (data: T) => void;
    /** 关闭模态框 */
    close: () => void;
    /** 设置加载状态 */
    setLoading: (loading: boolean) => void;
    /** 确认删除对话框 */
    confirmDelete: (options: {
        title?: string;
        content: string;
        onOk: () => Promise<void> | void;
    }) => void;
}

/**
 * 通用模态框 Hook
 * 
 * @returns 模态框状态和操作方法
 * 
 * @example
 * ```tsx
 * const { visible, isEdit, editingData, openCreate, openEdit, close, confirmDelete } = useModal<UserEntity>();
 * 
 * // 新建
 * <Button onClick={openCreate}>新建</Button>
 * 
 * // 编辑
 * <Button onClick={() => openEdit(record)}>编辑</Button>
 * 
 * // 删除确认
 * <Button onClick={() => confirmDelete({
 *   content: '确定删除该用户吗？',
 *   onOk: () => deleteUser(record.id)
 * })}>删除</Button>
 * 
 * // 模态框
 * <Modal open={visible} onCancel={close}>
 *   <UserForm user={isEdit ? editingData : undefined} />
 * </Modal>
 * ```
 */
export function useModal<T = unknown>(): UseModalReturn<T> {
    const [visible, setVisible] = useState(false);
    const [editingData, setEditingData] = useState<T | null>(null);
    const [loading, setLoading] = useState(false);

    const openCreate = useCallback(() => {
        setEditingData(null);
        setVisible(true);
    }, []);

    const openEdit = useCallback((data: T) => {
        setEditingData(data);
        setVisible(true);
    }, []);

    const close = useCallback(() => {
        setVisible(false);
        setEditingData(null);
        setLoading(false);
    }, []);

    const confirmDelete = useCallback(({
        title = '确认删除',
        content,
        onOk
    }: {
        title?: string;
        content: string;
        onOk: () => Promise<void> | void;
    }) => {
        Modal.confirm({
            title,
            content,
            okText: '确定',
            cancelText: '取消',
            okType: 'danger',
            onOk: async () => {
                try {
                    await onOk();
                    message.success('删除成功');
                } catch (error) {
                    message.error('删除失败');
                    throw error;
                }
            },
        });
    }, []);

    const isEdit = editingData !== null;

    return {
        visible,
        editingData,
        isEdit,
        loading,
        openCreate,
        openEdit,
        close,
        setLoading,
        confirmDelete,
    };
}

export default useModal;
