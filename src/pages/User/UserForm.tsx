import React, { useEffect } from 'react';
import {
  Form,
  Input,
  Select,
  Button,
  Row,
  Col,
  Upload,
  message,
  Space
} from 'antd';
import {
  PlusOutlined,
  UploadOutlined,
  UserOutlined
} from '@ant-design/icons';
import { createUserApi, updateUserApi, getUserDetailApi } from '../../api/user';
import type {
  UserEntity,
  UserCreateRequest,
  UserUpdateRequest,
  UserDetailResponse,
  DeptEntity,
  RoleEntity,
  PostEntity
} from '../../types';
import { UserStatus } from '../../types';

const { Option } = Select;
const { TextArea } = Input;

interface UserFormProps {
  user?: UserEntity | null;
  deptList: DeptEntity[];
  roleList: RoleEntity[];
  postList: PostEntity[];
  onSuccess: () => void;
  onCancel: () => void;
}

const UserForm: React.FC<UserFormProps> = ({
  user,
  deptList,
  roleList,
  postList,
  onSuccess,
  onCancel
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  const isEdit = !!user;

  // 获取用户详情（编辑时）
  useEffect(() => {
    if (isEdit && user) {
      const fetchUserDetail = async () => {
        try {
          const detail = await getUserDetailApi(user.userIden.userDomain, user.userIden.userId);
          form.setFieldsValue({
            ...detail,
            deptId: detail.deptId,
            roleIds: detail.roleIds,
            postIds: detail.postIds
          });
        } catch (error) {
          console.error('获取用户详情失败:', error);
          message.error('获取用户详情失败');
        }
      };
      fetchUserDetail();
    }
  }, [isEdit, user, form]);

  // 用户表单数据类型
  interface UserFormData {
    userDomain: string;
    loginName: string;
    realName: string;
    mobileNo: string;
    email: string;
    avatarUrl?: string;
    status: string;
    sex: string;
    description?: string;
    deptId: string;
    password: string;
    confirmPassword?: string;
    roleIds: string[];
    postIds: string[];
  }

  // 提交表单
  const handleSubmit = async (values: UserFormData) => {
    setLoading(true);
    try {
      if (isEdit) {
        // 更新用户
        const updateData: UserUpdateRequest = {
          userDomain: user!.userIden.userDomain,
          userId: user!.userIden.userId,
          loginName: values.loginName,
          realName: values.realName,
          mobileNo: values.mobileNo,
          email: values.email,
          avatarUrl: values.avatarUrl,
          status: values.status,
          sex: values.sex,
          description: values.description,
          deptId: values.deptId,
          password: values.password || '', // 编辑时密码可以为空
          roleIds: values.roleIds || [],
          postIds: values.postIds || []
        };
        await updateUserApi(updateData);
        message.success('用户更新成功');
      } else {
        // 创建用户
        const createData: UserCreateRequest = {
          userDomain: values.userDomain,
          loginName: values.loginName,
          realName: values.realName,
          mobileNo: values.mobileNo,
          email: values.email,
          avatarUrl: values.avatarUrl,
          status: values.status,
          sex: values.sex,
          description: values.description,
          deptId: values.deptId,
          password: values.password,
          roleIds: values.roleIds || [],
          postIds: values.postIds || []
        };
        await createUserApi(createData);
        message.success('用户创建成功');
      }
      onSuccess();
    } catch (error: unknown) {
      console.error('保存用户失败:', error);
      
      // 获取错误消息和字段错误
      let errorMessage = '保存用户失败';
      let fieldErrors: Record<string, string> = {};
      
      if (error && typeof error === 'object' && 'response' in error) {
        const errorResponse = error as { 
          response?: { 
            data?: { 
              message?: string;
              errorsMap?: Record<string, string>;
            } 
          } 
        };
        
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
        
        if (errorResponse.response?.data?.errorsMap) {
          fieldErrors = errorResponse.response.data.errorsMap;
        }
      }
      
      // 显示字段级错误
      if (Object.keys(fieldErrors).length > 0) {
        Object.keys(fieldErrors).forEach(field => {
          form.setFields([{
            name: field,
            errors: [fieldErrors[field]]
          }]);
        });
      }
      
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // 上传信息类型定义
  interface UploadInfo {
    file: {
      status: 'uploading' | 'done' | 'error' | 'removed';
      response?: string;
    };
  }

  // 头像上传
  const handleAvatarUpload = (info: UploadInfo) => {
    if (info.file.status === 'done') {
      form.setFieldsValue({ avatarUrl: info.file.response });
      message.success('头像上传成功');
    } else if (info.file.status === 'error') {
      message.error('头像上传失败');
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      initialValues={{
        status: UserStatus.ACTIVE,
        sex: '男',
        userDomain: 'default'
      }}
    >
      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="userDomain"
            label="用户域"
            rules={[{ required: true, message: '请选择用户域' }]}
          >
            <Select placeholder="请选择用户域" disabled={isEdit} style={{ width: 280 }}>
              <Option value="default">默认域</Option>
            </Select>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="loginName"
            label="登录名"
            rules={[
              { required: true, message: '请输入登录名' },
              { min: 3, message: '登录名至少3个字符' }
            ]}
          >
            <Input placeholder="请输入用户登录名称" />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="realName"
            label="真实姓名"
            rules={[{ required: true, message: '请输入真实姓名' }]}
          >
            <Input placeholder="请输入用户真实姓名" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="deptId"
            label="归属部门"
            rules={[{ required: true, message: '请选择归属部门' }]}
          >
            <Select placeholder="请选择归属部门" style={{ width: 280 }}>
              {deptList.map(dept => (
                <Option key={dept.id} value={dept.id}>
                  {dept.name}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
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
        </Col>
        <Col span={12}>
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
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: !isEdit, message: '请输入密码' },
              { min: 6, message: '密码至少6个字符' }
            ]}
          >
            <Input.Password placeholder={isEdit ? '留空则不修改密码' : '请输入密码'} maxLength={50} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="confirmPassword"
            label="确认密码"
            rules={[
              { required: !isEdit, message: '请输入确认密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次密码不一致'));
                }
              })
            ]}
          >
            <Input.Password placeholder={isEdit ? '留空则不修改密码' : '请再次输入密码'} maxLength={50} />
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="sex"
            label="性别"
            rules={[{ required: true, message: '请选择性别' }]}
          >
            <Select placeholder="请选择性别" style={{ width: 280 }}>
              <Option value="male">男</Option>
              <Option value="female">女</Option>
            </Select>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select placeholder="请选择状态" style={{ width: 280 }}>
              <Option value={UserStatus.ACTIVE}>正常</Option>
              <Option value={UserStatus.LOCK}>锁定</Option>
              <Option value={UserStatus.INACTIVE}>停用</Option>
            </Select>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={12}>
          <Form.Item
            name="postIds"
            label="岗位"
            rules={[{ required: true, message: '请选择岗位' }]}
          >
            <Select
              mode="multiple"
              placeholder="请选择岗位"
              style={{ width: 280 }}
              allowClear
            >
              {postList.map(post => (
                <Option key={post.id} value={post.id}>
                  {post.name}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="roleIds"
            label="角色"
            rules={[{ required: true, message: '请选择角色' }]}
          >
            <Select
              mode="multiple"
              placeholder="请选择角色"
              style={{ width: 280 }}
              allowClear
            >
              {roleList.map(role => (
                <Option key={role.id} value={role.id}>
                  {role.name}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={24}>
          <Form.Item
            name="avatarUrl"
            label="头像"
          >
            <Upload
              name="file"
              listType="picture-card"
              showUploadList={false}
              action="/api/upload"
              onChange={handleAvatarUpload}
            >
              <div>
                <UserOutlined />
                <div style={{ marginTop: 8 }}>上传头像</div>
              </div>
            </Upload>
          </Form.Item>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={24}>
          <Form.Item
            name="description"
            label="备注"
          >
            <TextArea
              rows={3}
              placeholder="请输入内容"
              maxLength={200}
            />
          </Form.Item>
        </Col>
      </Row>

      <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
        <Space>
          <Button onClick={onCancel}>
            取消
          </Button>
          <Button type="primary" htmlType="submit" loading={loading}>
            {isEdit ? '更新' : '创建'}
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default UserForm;
