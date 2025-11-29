import { useState, useEffect } from 'react';
import { Form, Input, Select, Upload, Button, Space, message, Avatar } from 'antd';
import { UserOutlined, UploadOutlined } from '@ant-design/icons';
import { getCurrentUserInfoApi, updateProfileApi, type UpdateProfileRequest } from '../../../api/auth';
import type { UserEntity } from '../../../types';

const { Option } = Select;
const { TextArea } = Input;

interface ProfileEditFormProps {
  onSuccess?: () => void;
  onCancel?: () => void;
}

function ProfileEditForm({ onSuccess, onCancel }: ProfileEditFormProps) {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(true);
  const [avatarUrl, setAvatarUrl] = useState<string>('');

  useEffect(() => {
    loadUserInfo();
  }, []);

  const loadUserInfo = async () => {
    try {
      setLoadingData(true);
      const user = await getCurrentUserInfoApi();
      const userData = user as unknown as UserEntity;
      form.setFieldsValue({
        realName: userData.realName,
        mobileNo: userData.mobileNo,
        email: userData.email,
        sex: userData.sex,
        description: userData.description,
        avatarUrl: userData.avatarUrl,
      });
      setAvatarUrl(userData.avatarUrl || '');
    } catch (error: any) {
      console.error('获取用户信息失败:', error);
      message.error(error?.message || '获取用户信息失败');
    } finally {
      setLoadingData(false);
    }
  };

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const updateData: UpdateProfileRequest = {
        realName: values.realName,
        mobileNo: values.mobileNo,
        email: values.email,
        avatarUrl: values.avatarUrl || avatarUrl,
        sex: values.sex,
        description: values.description,
      };

      await updateProfileApi(updateData);
      message.success('个人信息更新成功');
      onSuccess?.();
    } catch (error: any) {
      console.error('更新个人信息失败:', error);
      let errorMessage = '更新个人信息失败';
      
      if (error && typeof error === 'object' && 'response' in error) {
        const errorResponse = error as { response?: { data?: { message?: string } } };
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
      } else if (error && typeof error === 'object' && 'message' in error) {
        const errorWithMessage = error as { message: string };
        errorMessage = errorWithMessage.message;
      }
      
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleAvatarChange = (info: any) => {
    if (info.file.status === 'done') {
      const response = info.file.response;
      let url = '';
      if (response?.data) {
        url = typeof response.data === 'string' ? response.data : response.data.relativePath || response.data.absolutePath;
      } else if (typeof response === 'string') {
        url = response;
      } else if (response?.relativePath) {
        url = response.relativePath;
      } else if (response?.absolutePath) {
        url = response.absolutePath;
      }
      
      if (url) {
        setAvatarUrl(url);
        form.setFieldsValue({ avatarUrl: url });
        message.success('头像上传成功');
      } else {
        message.error('头像上传成功，但未获取到URL');
      }
    } else if (info.file.status === 'error') {
      message.error('头像上传失败');
    }
  };

  if (loadingData) {
    return <div>加载中...</div>;
  }

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      style={{ maxWidth: 600 }}
    >
      <Form.Item
        name="avatarUrl"
        label="头像"
      >
        <Space direction="vertical" align="center">
          <Avatar
            size={100}
            src={avatarUrl}
            icon={<UserOutlined />}
          />
          <Upload
            name="file"
            action="/api/common/file/upload2/original"
            showUploadList={false}
            onChange={handleAvatarChange}
            withCredentials
          >
            <Button icon={<UploadOutlined />}>上传头像</Button>
          </Upload>
        </Space>
      </Form.Item>

      <Form.Item
        name="realName"
        label="真实姓名"
        rules={[{ required: true, message: '请输入真实姓名' }]}
      >
        <Input placeholder="请输入真实姓名" />
      </Form.Item>

      <Form.Item
        name="mobileNo"
        label="手机号码"
        rules={[
          { required: true, message: '请输入手机号码' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
        ]}
      >
        <Input placeholder="请输入手机号码" maxLength={11} />
      </Form.Item>

      <Form.Item
        name="email"
        label="邮箱"
        rules={[
          { required: true, message: '请输入邮箱' },
          { type: 'email', message: '请输入正确的邮箱格式' }
        ]}
      >
        <Input placeholder="请输入邮箱" maxLength={50} />
      </Form.Item>

      <Form.Item
        name="sex"
        label="性别"
        rules={[{ required: true, message: '请选择性别' }]}
      >
        <Select placeholder="请选择性别">
          <Option value="M">男</Option>
          <Option value="F">女</Option>
          <Option value="male">男</Option>
          <Option value="female">女</Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="description"
        label="备注"
      >
        <TextArea
          rows={3}
          placeholder="请输入备注信息"
          maxLength={200}
          showCount
        />
      </Form.Item>

      <Form.Item>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            保存
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
}

export default ProfileEditForm;

