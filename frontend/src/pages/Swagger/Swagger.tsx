import React from 'react';
import { Card, Spin } from 'antd';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import './Swagger.css';
import { config } from '../../config/env';

/**
 * Swagger API 文档页面
 * 使用 swagger-ui-react 组件渲染，并应用自定义主题样式
 */
const Swagger: React.FC = () => {
  const [loading, setLoading] = React.useState(true);

  // OpenAPI 规范 JSON 地址
  const apiDocsUrl = `${config.BASE_PATH}/v3/api-docs`;

  return (
    <div className="fade-in" style={{ padding: '0' }}>
      <Card
        className="modern-card"
        styles={{
          body: {
            padding: '24px',
            background: 'rgba(255, 255, 255, 0.95)',
            minHeight: '400px', // 确保加载时有最小高度
            position: 'relative'
          }
        }}
      >
        {loading && (
          <div style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 10,
            background: 'rgba(255,255,255,0.8)',
            backdropFilter: 'blur(4px)'
          }}>
            <Spin size="large" tip="加载 API 文档中..." />
          </div>
        )}

        <div style={{ opacity: loading ? 0 : 1, transition: 'opacity 0.3s ease' }}>
          <SwaggerUI
            url={apiDocsUrl}
            docExpansion="list"
            defaultModelsExpandDepth={-1}
            persistAuthorization={true}
            tryItOutEnabled={true}
            filter={true}
            deepLinking={true}
            displayRequestDuration={true}
            showExtensions={true}
            showCommonExtensions={true}
            onComplete={() => {
              // 延迟关闭 Loading，防止闪烁，确保原生组件渲染完毕
              setTimeout(() => {
                setLoading(false);
              }, 500);
            }}
            requestInterceptor={(req) => {
              // 自动从 localStorage 获取 token 并添加到请求头
              const token = localStorage.getItem('token');
              if (token) {
                req.headers['x-access-token'] = token;
              }
              return req;
            }}
          />
        </div>
      </Card>
    </div>
  );
};

export default Swagger;

