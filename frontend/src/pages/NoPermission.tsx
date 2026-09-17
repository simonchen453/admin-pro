import { Result, Button } from 'antd';
import { useNavigate } from 'react-router-dom';
import { LockOutlined, HomeOutlined } from '@ant-design/icons';

function NoPermission() {
    const navigate = useNavigate();

    const handleGoHome = () => {
        navigate('/');
    };

    const handleGoBack = () => {
        navigate(-1);
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            background: 'var(--ap-bg)'
        }}>
            {/* 被拦下来这件事本身已经够醒目了，不需要再配一张紫色渐变的背景板 */}
            <Result
                status="403"
                icon={<LockOutlined style={{ color: 'var(--ap-text-3)' }} />}
                title="403"
                subTitle="抱歉，您没有权限访问此资源"
                extra={[
                    <Button 
                        type="primary" 
                        key="home" 
                        icon={<HomeOutlined />}
                        onClick={handleGoHome}
                        style={{ marginRight: 8 }}
                    >
                        返回首页
                    </Button>,
                    <Button 
                        key="back" 
                        onClick={handleGoBack}
                    >
                        返回上页
                    </Button>,
                ]}
            />
        </div>
    );
}

export default NoPermission;
