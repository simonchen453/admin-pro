import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@ant-design/v5-patch-for-react-19'
import './index.css'
import App from './App.tsx'
import 'normalize.css';
import 'antd/dist/reset.css'; /* Ant Design v5 推荐使用 reset */

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
