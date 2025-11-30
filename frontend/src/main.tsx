import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '@ant-design/v5-patch-for-react-19'
import './index.css'
import App from './App.tsx'
import 'normalize.css';
import 'antd/dist/reset.css'; /* Ant Design v5 推荐使用 reset */

// 全局兜底策略：监听所有输入框聚焦事件，强制关闭自动填充
// 注意：虽然这里做了全局处理，但在 Form 标签上显式添加 autoComplete="off" 仍然是更推荐的做法（兼容性更好）
document.addEventListener('focus', (e) => {
  const target = e.target as HTMLInputElement;
  if (target && target.tagName === 'INPUT' && !target.hasAttribute('autocomplete')) {
    target.setAttribute('autocomplete', 'off');
  }
}, true);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
