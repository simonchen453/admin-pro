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
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        }}>
            <Result
                status="403"
                icon={<LockOutlined style={{ color: '#ff4d4f' }} />}
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
