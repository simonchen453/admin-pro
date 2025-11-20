import { useState, useEffect, useImperativeHandle, forwardRef } from 'react';
import './Captcha.css';

interface CaptchaProps {
  onCaptchaChange?: (captchaKey: string) => void;
  className?: string;
}

export interface CaptchaRef {
  refresh: () => void;
}

const Captcha = forwardRef<CaptchaRef, CaptchaProps>(({ onCaptchaChange, className = '' }, ref) => {
  const [, setCaptchaKey] = useState<string>('');
  const [imageUrl, setImageUrl] = useState<string>('');

  // 生成随机key
  const generateCaptchaKey = () => {
    return Math.random().toString();
  };

  // 刷新验证码
  const refreshCaptcha = () => {
    const newKey = generateCaptchaKey();
    setCaptchaKey(newKey);
    // 直接使用完整的URL路径
    setImageUrl(`/api/rest/auth/captcha.jpg?key=${newKey}`);
    onCaptchaChange?.(newKey);
  };

  // 暴露刷新方法给父组件
  useImperativeHandle(ref, () => ({
    refresh: refreshCaptcha
  }));

  // 组件挂载时生成验证码
  useEffect(() => {
    refreshCaptcha();
  }, []);

  const handleImageClick = () => {
    refreshCaptcha();
  };

  return (
    <div className={`captcha-container ${className}`}>
      <img 
        src={imageUrl} 
        alt="验证码" 
        width="103" 
        height="40" 
        style={{ cursor: 'pointer' }}
        title="看不清可单击图片刷新"
        onClick={handleImageClick}
        onError={(e) => {
          console.error('验证码图片加载失败，URL:', imageUrl);
          // 不隐藏图片，而是显示错误信息
          e.currentTarget.alt = '验证码加载失败，点击重试';
        }}
      />
    </div>
  );
});

// 添加displayName用于调试
Captcha.displayName = 'Captcha';

export default Captcha;
