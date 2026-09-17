import React, { useState, useRef, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { EyeInvisibleOutlined, EyeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import { useAuthStore } from '../../stores/useUserStore.ts';
import Captcha, { type CaptchaRef } from '../../components/Captcha';
import { LogoMark } from '../../components/Logo';
import { getSystemInfoApi } from '../../api/common';
import type { LoginRequest, SystemInfo } from '../../types/index';
import './Login.css';

const loginSchema = z.object({
  loginName: z.string().min(1, '请输入用户名'),
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
      loginName: '',
      password: '',
      captcha: '',
      remember: false
    }
  });

  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    const loadSystemInfo = async () => {
      try {
        // 先加载系统信息，建立 session
        const response = await getSystemInfoApi();
        if (response.data) {
          setSystemInfo(response.data);
        }
      } catch (error) {
        console.error('获取系统信息失败:', error);
      }
    };
    // 先加载系统信息，建立 session，然后再加载验证码
    // 这样可以确保验证码使用同一个 session
    loadSystemInfo().then(() => {
      // 系统信息加载完成后，再刷新验证码，确保使用同一个 session
      setTimeout(() => {
        if (captchaRef.current) {
          captchaRef.current.refresh();
        }
      }, 50);
    });
  }, []);

  const onSubmit = async (data: LoginForm) => {
    setIsLoading(true);
    try {
      // 构建登录请求数据
      const loginData: LoginRequest = {
        loginName: data.loginName,
        password: data.password,
        domain: 'system',
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
    <div className="lg-page">
      {contextHolder}
      <div className="lg">

        <div className="lg-eyebrow">
          <span className="ap-logo"><LogoMark size={13} /></span>
          <b>{systemInfo?.platformShortName || 'AdminPro'}</b>
          {systemInfo?.platformName ? ` ${systemInfo.platformName}` : ' 企业级权限管理'}
        </div>

        <h1 className="lg-title">登录管理控制台</h1>

        {/* 登错环境在企业后台是真实且昂贵的事故，值得占一行显眼的位置。
            这里只写浏览器确实连着的主机名 —— 是「生产」还是「测试」前端无从得知，
            就不替它下判断，点也保持中性灰。 */}
        <div className="lg-env">
          <strong>当前站点</strong>
          <span>{window.location.host}</span>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="lg-form" autoComplete="off">
          {/* 隐藏输入框：绕开浏览器的自动填充 */}
          <input type="text" style={{ display: 'none' }} />
          <input type="password" style={{ display: 'none' }} />

          <div className="ap-field">
            <label className="ap-label" htmlFor="lg-user">登录名</label>
            <input
              {...register('loginName')}
              id="lg-user"
              type="text"
              className="ap-input ap-input-lg ap-input-mono"
              autoComplete="off"
              aria-invalid={errors.loginName ? 'true' : undefined}
              readOnly
              onFocus={(e) => e.target.removeAttribute('readonly')}
            />
            {errors.loginName && <span className="lg-err">{errors.loginName.message}</span>}
          </div>

          <div className="ap-field">
            <label className="ap-label" htmlFor="lg-pass">密码</label>
            <div className="lg-pw">
              <input
                {...register('password')}
                id="lg-pass"
                type={showPassword ? 'text' : 'password'}
                className="ap-input ap-input-lg"
                autoComplete="off"
                aria-invalid={errors.password ? 'true' : undefined}
                readOnly
                onFocus={(e) => e.target.removeAttribute('readonly')}
              />
              <button
                type="button"
                className="lg-pw-t"
                aria-label={showPassword ? '隐藏密码' : '显示密码'}
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOutlined /> : <EyeInvisibleOutlined />}
              </button>
            </div>
            {errors.password && <span className="lg-err">{errors.password.message}</span>}
          </div>

          <div className="ap-field">
            <label className="ap-label" htmlFor="lg-cap">验证码</label>
            <div className="lg-cap">
              <input
                {...register('captcha')}
                id="lg-cap"
                type="text"
                className="ap-input ap-input-lg ap-input-mono"
                autoComplete="off"
                inputMode="numeric"
                aria-invalid={errors.captcha ? 'true' : undefined}
              />
              <Captcha ref={captchaRef} onCaptchaChange={setCaptchaKey} className="lg-cap-img" />
            </div>
            {errors.captcha && <span className="lg-err">{errors.captcha.message}</span>}
          </div>

          <div className="lg-row">
            <label>
              <input {...register('remember')} type="checkbox" className="ap-check" />
              记住登录名
            </label>
            <a className="ap-link" href="#">忘记密码</a>
          </div>

          <button type="submit" className="ap-btn ap-btn-p ap-btn-lg ap-btn-block" disabled={isLoading}>
            {isLoading ? '登录中…' : '登录'}
          </button>
        </form>

        <div className="lg-foot">
          {(systemInfo?.releaseVersion || systemInfo?.buildVersion) && (
            <>
              {systemInfo.releaseVersion && <span>{systemInfo.releaseVersion}</span>}
              {systemInfo.releaseVersion && systemInfo.buildVersion && <span> · </span>}
              {systemInfo.buildVersion && <span>{systemInfo.buildVersion}</span>}
              <br />
            </>
          )}
          {systemInfo?.copyRight || `Copyright © ${new Date().getFullYear()} AdminPro`}
        </div>

      </div>
    </div>
  );
};

export default Login;
