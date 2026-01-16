import React from 'react';
import { Modal, Form, Input, Button, message } from 'antd';

/**
 * 重置密码表单数据
 */
interface ResetPasswordFormData {
    newPassword: string;
    confirmPassword: string;
}

/**
 * 重置密码模态框组件属性
 */
export interface ResetPasswordModalProps {
    /** 是否可见 */
    visible: boolean;
    /** 目标用户信息 */
    userName?: string;
    /** 是否正在加载 */
    loading?: boolean;
    /** 确认回调 */
    onConfirm: (newPassword: string) => void;
    /** 取消回调 */
    onCancel: () => void;
}

/**
 * 重置密码模态框组件
 * 
 * 独立的密码重置弹窗，包含密码验证逻辑
 */
const ResetPasswordModal: React.FC<ResetPasswordModalProps> = ({
    visible,
    userName,
    loading = false,
    onConfirm,
    onCancel
}) => {
    const [form] = Form.useForm<ResetPasswordFormData>();

    const handleOk = async () => {
        try {
            const values = await form.validateFields();
            if (values.newPassword !== values.confirmPassword) {
                message.error('两次输入的密码不一致');
                return;
            }
            onConfirm(values.newPassword);
            form.resetFields();
        } catch {
            // Validation failed
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onCancel();
    };

    return (
        <Modal
            title={`重置密码${userName ? ` - ${userName}` : ''}`}
            open={visible}
            onOk={handleOk}
            onCancel={handleCancel}
            confirmLoading={loading}
            destroyOnClose
        >
            <Form
                form={form}
                layout="vertical"
                autoComplete="off"
            >
                <Form.Item
                    name="newPassword"
                    label="新密码"
                    rules={[
                        { required: true, message: '请输入新密码' },
                        { min: 6, message: '密码长度不能少于6位' },
                        { max: 20, message: '密码长度不能超过20位' }
                    ]}
                >
                    <Input.Password placeholder="请输入新密码" />
                </Form.Item>
                <Form.Item
                    name="confirmPassword"
                    label="确认密码"
                    dependencies={['newPassword']}
                    rules={[
                        { required: true, message: '请确认密码' },
                        ({ getFieldValue }) => ({
                            validator(_, value) {
                                if (!value || getFieldValue('newPassword') === value) {
                                    return Promise.resolve();
                                }
                                return Promise.reject(new Error('两次输入的密码不一致'));
                            }
                        })
                    ]}
                >
                    <Input.Password placeholder="请再次输入密码" />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default ResetPasswordModal;
