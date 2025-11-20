import React, { useState, useRef, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { EyeInvisibleOutlined, EyeTwoTone, LockOutlined, UserOutlined, SafetyOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import { useAuthStore } from '../../stores/useUserStore.ts';
import Captcha, {type CaptchaRef } from '../../components/Captcha';
import { getSystemInfoApi } from '../../api/common';
import type { LoginRequest, SystemInfo } from '../../types/index';
import './Login.css';

const loginSchema = z.object({
  userId: z.string().min(1, '请输入用户ID'),
  password: z.string().min(1, '请输入密码'),
  captcha: z.string().min(1, '请输入验证码'),
  remember: z.boolean().optional()
});

type LoginForm = z.infer<typeof loginSchema>;

const Login: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [captchaKey, setCaptchaKey] = useState<string>('');
  const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);
  const captchaRef = useRef<CaptchaRef>(null);
  const navigate = useNavigate();
  const { login } = useAuthStore();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      userId: '',
      password: '',
      captcha: '',
      remember: false
    }
  });

  const [messageApi, contextHolder] = message.useMessage();

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

  const onSubmit = async (data: LoginForm) => {
    setIsLoading(true);
    try {
      // 构建登录请求数据
      const loginData: LoginRequest = {
        userId: data.userId,
        password: data.password,
        platform: 'SYSTEM',
        captcha: data.captcha,
        captchaKey: captchaKey
      };
      
      // 调用登录API
      await login(loginData);
      
      messageApi.success('登录成功！');
      
      // 跳转到首页
      navigate('/home', { replace: true });
    } catch (error: unknown) {
      console.error('登录失败:', error);
      
      // 获取错误消息 - 优先使用服务器返回的message
      let errorMessage = '登录失败，请检查用户名、密码和验证码';
      
      if (error && typeof error === 'object' && 'response' in error) {
        const errorResponse = error as { response?: { data?: { message?: string } } };
        if (errorResponse.response?.data?.message) {
          errorMessage = errorResponse.response.data.message;
        }
      } else if (error && typeof error === 'object' && 'message' in error) {
        const errorWithMessage = error as { message: string };
        errorMessage = errorWithMessage.message;
      }
      
      // 显示错误消息
      messageApi.error(errorMessage);
      
      // 自动刷新验证码
      if (captchaRef.current) {
        captchaRef.current.refresh();
      }
      
      // 清空验证码输入框
      setValue('captcha', '');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="login-container">
      {contextHolder}
      <div className="login-background">
        <div className="tech-grid"></div>
        <div className="floating-particles">
          {Array.from({ length: 20 }).map((_, i) => (
            <div key={i} className={`particle particle-${i + 1}`}></div>
          ))}
        </div>
      </div>
      
      <div className="login-card">
        <div className="login-header">
          <div className="brand-section">
            <div className="logo-wrapper">
              <div className="login-logo-background">
                <img src="/logo.svg" alt="Admin Pro" className="logo-image" />
              </div>
            </div>
            <div className="login-brand-text">
              <h1 className="login-brand-title">{systemInfo?.platformShortName || 'Admin Pro'}</h1>
              <div className="brand-subtitle">
                <span className="subtitle-text">企业级管理系统</span>
                <div className="subtitle-line"></div>
              </div>
            </div>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="login-form">
          <div className="form-group">
            <div className="input-wrapper">
              <UserOutlined className="input-icon" />
              <input
                {...register('userId')}
                type="text"
                placeholder="请输入用户ID"
                className={`form-input ${errors.userId ? 'error' : ''}`}
                autoComplete="username"
              />
            </div>
            {errors.userId && (
              <span className="error-message">{errors.userId.message}</span>
            )}
          </div>

          <div className="form-group">
            <div className="input-wrapper">
              <LockOutlined className="input-icon" />
              <input
                {...register('password')}
                type={showPassword ? 'text' : 'password'}
                placeholder="请输入密码"
                className={`form-input ${errors.password ? 'error' : ''}`}
                autoComplete="current-password"
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeTwoTone /> : <EyeInvisibleOutlined />}
              </button>
            </div>
            {errors.password && (
              <span className="error-message">{errors.password.message}</span>
            )}
          </div>

          <div className="form-group">
            <div className="captcha-input-wrapper">
              <div className="input-wrapper captcha-input">
                <SafetyOutlined className="input-icon" />
                <input
                  {...register('captcha')}
                  type="text"
                  placeholder="请输入验证码"
                  className={`form-input ${errors.captcha ? 'error' : ''}`}
                  autoComplete="off"
                />
              </div>
              <Captcha 
                ref={captchaRef}
                onCaptchaChange={setCaptchaKey}
                className="captcha-component"
              />
            </div>
            {errors.captcha && (
              <span className="error-message">{errors.captcha.message}</span>
            )}
          </div>

          <div className="form-options">
            <label className="remember-me">
              <input
                {...register('remember')}
                type="checkbox"
                className="checkbox"
              />
              <span className="checkbox-custom"></span>
              <span className="checkbox-text">记住我</span>
            </label>
            <a href="#" className="forgot-password">忘记密码？</a>
          </div>

          <button
            type="submit"
            className={`login-button ${isLoading ? 'loading' : ''}`}
            disabled={isLoading}
          >
            {isLoading ? (
              <div className="loading-spinner"></div>
            ) : (
              '登录'
            )}
          </button>
        </form>

        <div className="login-footer">
          <p className="copyright">
            {systemInfo?.copyRight || `© ${new Date().getFullYear()} 管理系统. All rights reserved.`}
          </p>
          {(systemInfo?.releaseVersion || systemInfo?.buildVersion) && (
            <p className="copyright" style={{ marginTop: '6px', fontSize: '11px' }}>
              {systemInfo.releaseVersion && <span>版本: {systemInfo.releaseVersion}</span>}
              {systemInfo.releaseVersion && systemInfo.buildVersion && <span> | </span>}
              {systemInfo.buildVersion && <span>构建: {systemInfo.buildVersion}</span>}
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

export default Login;
