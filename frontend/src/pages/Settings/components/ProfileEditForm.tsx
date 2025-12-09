import { useState, useEffect } from 'react';
import { Form, Input, Select, Upload, Button, Space, Avatar, App, Spin, Row, Col } from 'antd';
import { UserOutlined, UploadOutlined, CheckCircleFilled } from '@ant-design/icons';
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
  const { message } = App.useApp();
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
    if (info.file.status === 'uploading') {
      return;
    }

    if (info.file.status === 'done') {
      const response = info.file.response;
      let url = '';

      // 处理响应数据，可能的结构：
      // 1. { restCode: '200', data: { relativePath: '...', absolutePath: '...' } }
      // 2. { data: { relativePath: '...', absolutePath: '...' } }
      // 3. { relativePath: '...', absolutePath: '...' }
      // 4. 直接是字符串URL

      if (response) {
        // 优先使用 absolutePath（完整URL），如果没有则使用 relativePath
        if (response.data) {
          if (typeof response.data === 'string') {
            url = response.data;
          } else if (response.data.absolutePath) {
            url = response.data.absolutePath;
          } else if (response.data.relativePath) {
            // 如果是相对路径，需要拼接baseURL
            const baseURL = import.meta.env.VITE_API_BASE || '/api';
            url = baseURL.replace('/api', '') + response.data.relativePath;
          }
        } else if (response.absolutePath) {
          url = response.absolutePath;
        } else if (response.relativePath) {
          const baseURL = import.meta.env.VITE_API_BASE || '/api';
          url = baseURL.replace('/api', '') + response.relativePath;
        } else if (typeof response === 'string') {
          url = response;
        }
      }

      if (url) {
        setAvatarUrl(url);
        form.setFieldsValue({ avatarUrl: url });
        message.success('头像上传成功');
      } else {
        console.error('上传响应数据:', response);
        message.error('头像上传成功，但未获取到URL');
      }
    } else if (info.file.status === 'error') {
      console.error('上传失败:', info.file.error);
      message.error('头像上传失败');
    }
  };

  return (
    <Spin spinning={loadingData}>
      {/* 标题部分 */}
      <div style={{ marginBottom: 24 }}>
        <div style={{ fontSize: '16px', fontWeight: 500, color: '#1f1f1f', marginBottom: 4 }}>基本信息</div>
        <div style={{ fontSize: '14px', color: '#8c8c8c' }}>更新您的头像和个人详细信息。</div>
      </div>

      <Form autoComplete="off"
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
      >
        <Row gutter={48}>
          {/* 左侧头像 */}
          <Col xs={24} md={6} style={{ textAlign: 'center' }}>
            <Form.Item name="avatarUrl" style={{ marginBottom: 0 }}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <div style={{
                  position: 'relative',
                  width: 120,
                  height: 120,
                  marginBottom: 16,
                  cursor: 'pointer'
                }}>
                  <Avatar
                    size={120}
                    src={avatarUrl || undefined}
                    icon={<UserOutlined />}
                    style={{ border: '1px solid #f0f0f0' }}
                  />
                  <Upload
                    name="file"
                    action="/api/common/file/upload2/original"
                    showUploadList={false}
                    onChange={handleAvatarChange}
                    withCredentials
                    accept="image/*"
                    beforeUpload={(file) => {
                      const isImage = file.type.startsWith('image/');
                      if (!isImage) {
                        message.error('只能上传图片文件！');
                        return Upload.LIST_IGNORE;
                      }
                      const isLt5M = file.size / 1024 / 1024 < 5;
                      if (!isLt5M) {
                        message.error('图片大小不能超过 5MB！');
                        return Upload.LIST_IGNORE;
                      }
                      return true;
                    }}
                  >
                    <div style={{
                      position: 'absolute',
                      bottom: 0,
                      right: 0,
                      width: 32,
                      height: 32,
                      background: '#fff',
                      borderRadius: '50%',
                      boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      cursor: 'pointer',
                      color: '#5b73e8'
                    }}>
                      <UploadOutlined />
                    </div>
                  </Upload>
                </div>
                <div style={{ color: '#666', fontSize: '12px', marginTop: 8 }}>点击更换头像</div>
              </div>
            </Form.Item>
          </Col>

          {/* 右侧表单 */}
          <Col xs={24} md={18}>
            <Row gutter={24}>
              <Col xs={24} sm={12}>
                <Form.Item
                  name="realName"
                  label="用户姓名"
                  rules={[{ required: true, message: '请输入用户姓名' }]}
                >
                  <Input placeholder="请输入用户姓名" allowClear size="large" />
                </Form.Item>
              </Col>

              <Col xs={24} sm={12}>
                <Form.Item
                  name="mobileNo"
                  label="手机号码"
                  rules={[
                    { required: true, message: '请输入手机号码' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
                  ]}
                >
                  <Input placeholder="请输入手机号码" maxLength={11} allowClear size="large" />
                </Form.Item>
              </Col>

              <Col span={24}>
                <Form.Item
                  name="email"
                  label="联系邮箱"
                  rules={[
                    { required: true, message: '请输入邮箱' },
                    { type: 'email', message: '请输入正确的邮箱格式' }
                  ]}
                >
                  <Input placeholder="请输入邮箱" maxLength={50} allowClear size="large" />
                </Form.Item>
              </Col>

              <Col span={24}>
                <Form.Item
                  name="description"
                  label="个人简介"
                >
                  <TextArea
                    autoSize={{ minRows: 4, maxRows: 8 }}
                    placeholder="请输入个人简介..."
                    maxLength={200}
                    showCount
                    allowClear
                    size="large"
                  />
                </Form.Item>
              </Col>

              <Col span={24} style={{ textAlign: 'right', marginTop: 12 }}>
                <Button type="primary" htmlType="submit" loading={loading} size="large" icon={<CheckCircleFilled />}>
                  保存更改
                </Button>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
    </Spin>
  );
}

export default ProfileEditForm;

