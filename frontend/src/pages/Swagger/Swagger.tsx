import React from 'react';
import { Breadcrumb, Button, Card, Space } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const Swagger: React.FC = () => {
  const navigate = useNavigate();

  const swaggerUrl = '/swagger-ui/index.html';

  return (
    <div className="fade-in" style={{ padding: '0' }}>


      <Card className="modern-card" styles={{ body: { padding: 0 } }}>
        <iframe
          src={swaggerUrl}
          frameBorder="0"
          scrolling="yes"
          style={{
            width: '100%',
            minHeight: 'calc(100vh - 150px)',
            border: 'none',
            borderRadius: '0 0 8px 8px'
          }}
          title="Swagger UI"
        />
      </Card>
    </div>
  );
};

export default Swagger;
