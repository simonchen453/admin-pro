import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import { config } from '../../config/env';

const Swagger = () => {
    // Point to the OpenAPI JSON endpoint (usually /v3/api-docs for SpringDoc)
    // Ensure this path is correctly proxied or accessible
    const specUrl = `${config.API_URL}/v3/api-docs`;

    return (
        <div style={{ height: 'calc(100vh - 100px)', overflow: 'auto' }}>
            <SwaggerUI url={specUrl} />
        </div>
    );
};

export default Swagger;
