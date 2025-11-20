import React from 'react';
import { Breadcrumb, Button, Card } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const Swagger: React.FC = () => {
  const navigate = useNavigate();
  
  const swaggerUrl = '/swagger-ui/index.html';

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center' }}>
        <Breadcrumb
          items={[
            {
              title: (
                <Button
                  type="link"
                  icon={<HomeOutlined />}
                  onClick={() => navigate('/')}
                  style={{ padding: 0, height: 'auto', lineHeight: 1 }}
                >
                  首页
                </Button>
              ),
            },
            {
              title: '接口文档管理',
            },
          ]}
        />
      </div>

      <Card>
        <iframe
          src={swaggerUrl}
          frameBorder="0"
          scrolling="yes"
          style={{
            width: '100%',
            minHeight: '900px',
            border: 'none',
          }}
          title="Swagger UI"
        />
      </Card>
    </div>
  );
};

export default Swagger;

