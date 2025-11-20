import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  message, 
  Space,
  Typography,
  Divider,
  Row,
  Col
} from 'antd';
import { 
  LockOutlined, 
  EyeInvisibleOutlined, 
  EyeTwoTone,
  CheckCircleOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { changePasswordApi } from '../../api/auth';
import './ChangePassword.css';

const { Title, Text } = Typography;

const changePasswordSchema = z.object({
  currentPassword: z.string().min(1, '请输入当前密码'),
  newPassword: z.string()
    .min(8, '新密码至少8位')
    .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/, '密码必须包含大小写字母、数字和特殊字符'),
  confirmPassword: z.string().min(1, '请确认新密码')
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: "两次输入的密码不一致",
  path: ["confirmPassword"],
});

type ChangePasswordForm = z.infer<typeof changePasswordSchema>;

const ChangePassword: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    reset
  } = useForm<ChangePasswordForm>({
    resolver: zodResolver(changePasswordSchema),
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  });

  const [messageApi, contextHolder] = message.useMessage();

  const onSubmit = async (data: ChangePasswordForm) => {
    setIsLoading(true);
    try {
      await changePasswordApi({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword
      });
      
      messageApi.success('密码修改成功！');
      reset();
      navigate('/');
    } catch (error: unknown) {
      console.error('修改密码失败:', error);
      
      // 获取错误消息 - 优先使用服务器返回的message
      let errorMessage = '修改密码失败，请重试';
      
      if (error && typeof error === 'object' && 'response' in error) {
        const errorResponse = error as { response?: { data?: { message?: string } } };
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
      } else if (error && typeof error === 'object' && 'message' in error) {
        const errorWithMessage = error as { message: string };
        errorMessage = errorWithMessage.message;
      }
      
      messageApi.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  // 密码强度类型定义
  interface PasswordStrength {
    score: number;
    text: string;
    color: string;
  }

  const passwordStrength = (password: string): PasswordStrength => {
    if (!password) return { score: 0, text: '', color: '' };
    
    let score = 0;
    if (password.length >= 8) score++;
    if (/[a-z]/.test(password)) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[@$!%*?&]/.test(password)) score++;
    
    if (score <= 2) return { score, text: '弱', color: '#ff4d4f' };
    if (score <= 3) return { score, text: '中', color: '#faad14' };
    return { score, text: '强', color: '#52c41a' };
  };

  const newPassword = watch('newPassword');
  const strength = passwordStrength(newPassword);

  return (
    <div className="change-password-container">
      {contextHolder}
      <div className="change-password-content">
        <Card className="change-password-card">
          <div className="change-password-header">
            <div className="logo-wrapper">
              <div className="changepwd-logo-background">
                <img src="/logo.svg" alt="Admin Pro" className="changepwd-logo-image" />
              </div>
            </div>
            <div className="changepwd-brand-text">
              <h1 className="changepwd-brand-title">修改密码</h1>
              <p className="changepwd-brand-subtitle">为了您的账户安全，请定期修改密码</p>
            </div>
          </div>
          
          <Form
            layout="vertical"
            onFinish={handleSubmit(onSubmit)}
            className="change-password-form"
          >
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <Form.Item
                  label="当前密码"
                  validateStatus={errors.currentPassword ? 'error' : ''}
                  help={errors.currentPassword?.message}
                >
                  <Input.Password
                    {...register('currentPassword')}
                    placeholder="请输入当前密码"
                    prefix={<LockOutlined />}
                    iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                    size="large"
                  />
                </Form.Item>
              </Col>
              
              <Col span={24}>
                <Form.Item
                  label="新密码"
                  validateStatus={errors.newPassword ? 'error' : ''}
                  help={errors.newPassword?.message}
                >
                  <Input.Password
                    {...register('newPassword')}
                    placeholder="请输入新密码"
                    prefix={<LockOutlined />}
                    iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                    size="large"
                  />
                  {newPassword && (
                    <div className="password-strength">
                      <div className="strength-bar">
                        <div 
                          className="strength-fill" 
                          style={{ 
                            width: `${(strength.score / 5) * 100}%`,
                            backgroundColor: strength.color
                          }}
                        />
                      </div>
                      <Text 
                        style={{ color: strength.color, fontSize: '12px' }}
                      >
                        密码强度: {strength.text}
                      </Text>
                    </div>
                  )}
                </Form.Item>
              </Col>
              
              <Col span={24}>
                <Form.Item
                  label="确认新密码"
                  validateStatus={errors.confirmPassword ? 'error' : ''}
                  help={errors.confirmPassword?.message}
                >
                  <Input.Password
                    {...register('confirmPassword')}
                    placeholder="请再次输入新密码"
                    prefix={<LockOutlined />}
                    iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                    size="large"
                  />
                </Form.Item>
              </Col>
            </Row>
            
            <Divider />
            
            <div className="password-requirements">
              <Title level={5}>密码要求：</Title>
              <ul>
                <li>至少8位字符</li>
                <li>包含大写字母</li>
                <li>包含小写字母</li>
                <li>包含数字</li>
                <li>包含特殊字符 (@$!%*?&)</li>
              </ul>
            </div>
            
            <Form.Item className="form-actions">
              <Space size="middle">
                <Button 
                  type="primary" 
                  htmlType="submit" 
                  loading={isLoading}
                  icon={<CheckCircleOutlined />}
                  size="large"
                >
                  确认修改
                </Button>
                <Button 
                  onClick={() => navigate('/')}
                  size="large"
                >
                  取消
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  );
};

export default ChangePassword;
