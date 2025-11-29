import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { 
  Form, 
  Input, 
  Button, 
  message, 
  Space,
  Typography,
  Card,
  Row,
  Col,
  Alert
} from 'antd';
import { 
  LockOutlined, 
  EyeInvisibleOutlined, 
  EyeTwoTone,
  CheckCircleOutlined,
  SafetyOutlined
} from '@ant-design/icons';
import { changePasswordApi } from '../../../api/auth';

const { Text } = Typography;

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

const ChangePasswordForm: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);

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
    } catch (error: unknown) {
      console.error('修改密码失败:', error);
      
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
    <div>
      {contextHolder}
      <Card 
        title={
          <Space>
            <SafetyOutlined />
            <span>修改密码</span>
          </Space>
        }
      >
        <Alert
          message="密码安全提示"
          description="为了您的账户安全，请定期更换密码，并使用强密码。"
          type="info"
          icon={<SafetyOutlined />}
          showIcon
          style={{ marginBottom: 24 }}
        />

        <Form
          layout="vertical"
          onFinish={handleSubmit(onSubmit)}
          style={{ maxWidth: 600 }}
        >
          <Row gutter={[0, 16]}>
            <Col span={24}>
              <Form.Item
                label="当前密码"
                validateStatus={errors.currentPassword ? 'error' : ''}
                help={errors.currentPassword?.message}
                required
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
                required
              >
                <Input.Password
                  {...register('newPassword')}
                  placeholder="请输入新密码"
                  prefix={<LockOutlined />}
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                />
                {newPassword && (
                  <div style={{ marginTop: 8 }}>
                    <div style={{ 
                      height: 4, 
                      backgroundColor: '#f0f0f0', 
                      borderRadius: 2,
                      overflow: 'hidden'
                    }}>
                      <div 
                        style={{ 
                          height: '100%',
                          width: `${(strength.score / 5) * 100}%`,
                          backgroundColor: strength.color,
                          transition: 'all 0.3s'
                        }}
                      />
                    </div>
                    <Text 
                      style={{ color: strength.color, fontSize: '12px', marginTop: 4, display: 'block' }}
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
                required
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
          
          <Card 
            size="small" 
            style={{ 
              marginTop: 16, 
              marginBottom: 24,
              backgroundColor: '#fafafa'
            }}
            title={<Text strong>密码要求</Text>}
          >
            <ul style={{ margin: 0, paddingLeft: 20 }}>
              <li>至少8位字符</li>
              <li>包含大写字母</li>
              <li>包含小写字母</li>
              <li>包含数字</li>
              <li>包含特殊字符 (@$!%*?&)</li>
            </ul>
          </Card>
          
          <Form.Item style={{ marginBottom: 0 }}>
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
                onClick={() => reset()}
                size="large"
              >
                重置
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default ChangePasswordForm;

