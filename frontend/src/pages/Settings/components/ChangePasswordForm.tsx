import { useState, useEffect } from 'react';
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
import type { Rule } from 'antd/es/form';
import { 
  LockOutlined, 
  EyeInvisibleOutlined, 
  EyeTwoTone,
  CheckCircleOutlined,
  SafetyOutlined
} from '@ant-design/icons';
import { changePasswordApi, getPasswordRuleApi, type PasswordRule } from '../../../api/auth';

const { Text } = Typography;

const ChangePasswordForm: React.FC = () => {
  const [form] = Form.useForm();
  const [isLoading, setIsLoading] = useState(false);
  const [passwordRule, setPasswordRule] = useState<PasswordRule | null>(null);
  
  // 使用 Form.useWatch 监听字段值的变化
  const newPasswordValue = Form.useWatch('newPassword', form);
  const confirmPasswordValue = Form.useWatch('confirmPassword', form);

  // 加载密码规则
  useEffect(() => {
    const loadPasswordRule = async () => {
      try {
        const rule = await getPasswordRuleApi();
        setPasswordRule(rule);
      } catch (error) {
        console.error('获取密码规则失败:', error);
        // 使用默认规则
        setPasswordRule({
          minLength: 8,
          maxLength: 20,
          requireLowerCase: true,
          requireUpperCase: true,
          requireDigit: true,
          requireSpecialChar: true,
          specialChars: '@$!%*?&'
        });
      }
    };
    loadPasswordRule();
  }, []);

  // 当新密码字段值变化时，如果之前有错误且现在有值，清除错误消息（提供更好的输入体验）
  useEffect(() => {
    console.log('newPasswordValue changed: ', newPasswordValue);
    if (newPasswordValue) {
      const errors = form.getFieldError('newPassword');
      console.log('newPassword errors: ', errors);
      if (errors.length > 0) {
        console.log('clean the newPassword error msg');
        // 延迟清除，避免与验证冲突
        const timer = setTimeout(() => {
          form.setFields([{ name: 'newPassword', errors: [] }]);
        }, 100);
        return () => clearTimeout(timer);
      }
    }
    // 移除 form 作为依赖项，因为 form 对象是稳定的
  }, [newPasswordValue]);

  // 当确认密码字段值变化时，如果之前有错误且现在有值，清除错误消息
  useEffect(() => {
    console.log('confirmPasswordValue changed: ', confirmPasswordValue);
    if (confirmPasswordValue) {
      const errors = form.getFieldError('confirmPassword');
      console.log('confirmPassword errors: ', errors);
      if (errors.length > 0) {
        console.log('clean the confirmPassword error msg');
        // 延迟清除，避免与验证冲突
        const timer = setTimeout(() => {
          form.setFields([{ name: 'confirmPassword', errors: [] }]);
        }, 100);
        return () => clearTimeout(timer);
      }
    }
    // 移除 form 作为依赖项，因为 form 对象是稳定的
  }, [confirmPasswordValue]);

  const handleSubmit = async (values: any) => {
    setIsLoading(true);
    try {
      await changePasswordApi({
        oldPwd: values.currentPassword,
        newPwd: values.newPassword,
        confirmNewPwd: values.confirmPassword
      });
      
      message.success('密码修改成功！');
      form.resetFields();
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
      
      message.error(errorMessage);
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
    if (!password || !passwordRule) return { score: 0, text: '', color: '' };
    
    let score = 0;
    const rule = passwordRule;
    
    if (password.length >= rule.minLength) score++;
    if (rule.requireLowerCase && /[a-z]/.test(password)) score++;
    if (rule.requireUpperCase && /[A-Z]/.test(password)) score++;
    if (rule.requireDigit && /\d/.test(password)) score++;
    if (rule.requireSpecialChar) {
      const specialCharsPattern = rule.specialChars.split('').map(char => 
        char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
      ).join('');
      if (new RegExp(`[${specialCharsPattern}]`).test(password)) score++;
    }
    
    const maxScore = 1 + (rule.requireLowerCase ? 1 : 0) + (rule.requireUpperCase ? 1 : 0) + 
                     (rule.requireDigit ? 1 : 0) + (rule.requireSpecialChar ? 1 : 0);
    
    if (score <= maxScore * 0.4) return { score, text: '弱', color: '#ff4d4f' };
    if (score <= maxScore * 0.7) return { score, text: '中', color: '#faad14' };
    return { score, text: '强', color: '#52c41a' };
  };

  const strength = passwordStrength(newPasswordValue || '');

  // 根据密码规则生成验证规则
  const getPasswordValidationRules = () => {
    if (!passwordRule) {
      return [{
        validator: (_: Rule, value: string) => {
          if (!value) {
            return Promise.reject(new Error('请输入新密码'));
          }
          return Promise.resolve();
        }
      }];
    }

    const rule = passwordRule;
    return [{
      validator: (_: Rule, value: string) => {
        if (!value) {
          return Promise.reject(new Error('请输入新密码'));
        }
        
        const errors: string[] = [];
        
        if (value.length < rule.minLength) {
          errors.push(`新密码至少${rule.minLength}位`);
        }
        if (rule.maxLength && value.length > rule.maxLength) {
          errors.push(`新密码最多${rule.maxLength}位`);
        }
        if (rule.requireLowerCase && !/[a-z]/.test(value)) {
          errors.push('密码必须包含小写字母');
        }
        if (rule.requireUpperCase && !/[A-Z]/.test(value)) {
          errors.push('密码必须包含大写字母');
        }
        if (rule.requireDigit && !/\d/.test(value)) {
          errors.push('密码必须包含数字');
        }
        if (rule.requireSpecialChar) {
          const specialCharsPattern = rule.specialChars.split('').map(char => 
            char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
          ).join('');
          if (!new RegExp(`[${specialCharsPattern}]`).test(value)) {
            errors.push(`密码必须包含特殊字符 (${rule.specialChars})`);
          }
        }
        
        if (errors.length > 0) {
          return Promise.reject(new Error(errors[0]));
        }
        
        return Promise.resolve();
      }
    }];
  };

  return (
    <div>
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

        <Form autoComplete="off"
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          validateTrigger={['onBlur', 'onSubmit']}
          style={{ maxWidth: 600 }}
        >
          <Row gutter={[0, 16]}>
            <Col span={24}>
              <Form.Item
                name="currentPassword"
                label="当前密码"
                rules={[
                  { required: true, message: '请输入当前密码' }
                ]}
              >
                <Input.Password
                  placeholder="请输入当前密码"
                  prefix={<LockOutlined />}
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                />
              </Form.Item>
            </Col>
            
            <Col span={24}>
              <Form.Item
                name="newPassword"
                label="新密码"
                rules={getPasswordValidationRules()}
              >
                <Input.Password
                  placeholder="请输入新密码"
                  prefix={<LockOutlined />}
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                  onChange={(e) => {
                    // 确保 Form.useWatch 能监听到变化
                    // 不在这里设置值，让 Form 自己管理
                    const value = e.target.value;
                    // 如果字段有错误且现在有值，清除错误
                    if (value) {
                      const errors = form.getFieldError('newPassword');
                      if (errors.length > 0) {
                        setTimeout(() => {
                          form.setFields([{ name: 'newPassword', errors: [] }]);
                        }, 100);
                      }
                    }
                  }}
                />
              </Form.Item>
              {newPasswordValue && (
                <div style={{ marginTop: -16, marginBottom: 16 }}>
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
            </Col>
            
            <Col span={24}>
              <Form.Item
                name="confirmPassword"
                label="确认新密码"
                dependencies={['newPassword']}
                rules={[
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value) {
                        return Promise.reject(new Error('请确认新密码'));
                      }
                      const newPwd = getFieldValue('newPassword');
                      if (!newPwd) {
                        return Promise.reject(new Error('请先输入新密码'));
                      }
                      if (newPwd !== value) {
                        return Promise.reject(new Error('两次输入的密码不一致'));
                      }
                      return Promise.resolve();
                    },
                  }),
                ]}
              >
                <Input.Password
                  placeholder="请再次输入新密码"
                  prefix={<LockOutlined />}
                  iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
                  size="large"
                  onChange={(e) => {
                    // 确保 Form.useWatch 能监听到变化
                    // 不在这里设置值，让 Form 自己管理
                    const value = e.target.value;
                    // 如果字段有错误且现在有值，清除错误
                    if (value) {
                      const errors = form.getFieldError('confirmPassword');
                      if (errors.length > 0) {
                        setTimeout(() => {
                          form.setFields([{ name: 'confirmPassword', errors: [] }]);
                        }, 100);
                      }
                    }
                  }}
                />
              </Form.Item>
              {confirmPasswordValue && newPasswordValue && (
                <div style={{ marginTop: -16, marginBottom: 16 }}>
                  {confirmPasswordValue === newPasswordValue ? (
                    <Text style={{ color: '#52c41a', fontSize: '12px' }}>
                      ✓ 密码一致
                    </Text>
                  ) : (
                    <Text style={{ color: '#ff4d4f', fontSize: '12px' }}>
                      ✗ 两次输入的密码不一致
                    </Text>
                  )}
                </div>
              )}
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
            {passwordRule ? (
              <ul style={{ margin: 0, paddingLeft: 20 }}>
                <li>至少{passwordRule.minLength}位字符{passwordRule.maxLength ? `，最多${passwordRule.maxLength}位` : ''}</li>
                {passwordRule.requireLowerCase && <li>包含小写字母</li>}
                {passwordRule.requireUpperCase && <li>包含大写字母</li>}
                {passwordRule.requireDigit && <li>包含数字</li>}
                {passwordRule.requireSpecialChar && <li>包含特殊字符 ({passwordRule.specialChars})</li>}
              </ul>
            ) : (
              <ul style={{ margin: 0, paddingLeft: 20 }}>
                <li>加载中...</li>
              </ul>
            )}
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
                onClick={() => form.resetFields()}
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

