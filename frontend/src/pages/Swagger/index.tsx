import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import { config } from '../../config/env';

const Swagger = () => {
    // 使用 BASE_PATH 构建 specUrl，确保通过代理请求后端
    const specUrl = `${config.BASE_PATH}/v3/api-docs`;

    return (
        <div style={{ height: 'calc(100vh - 100px)', overflow: 'auto' }}>
            <SwaggerUI url={specUrl} />
        </div>
    );
};

export default Swagger;
