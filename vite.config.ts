import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  build: {
    target: 'es2015',
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    minify: 'terser',
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom'],
          antd: ['antd', '@ant-design/icons'],
          router: ['react-router-dom'],
          utils: ['axios', 'zustand'],
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
  server: {
    port: 3000,
    open: true,
    cors: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/ecqee'),
        configure: (proxy, _options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            console.log('Sending Request to the Target:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, _res) => {
            console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
            const setCookie = proxyRes.headers['set-cookie'];
            if (Array.isArray(setCookie)) {
              proxyRes.headers['set-cookie'] = setCookie.map((cookie) =>
                cookie
                  .replace(/;\s*Path=\/hyx/gi, '; Path=/')
              );
            } else if (typeof setCookie === 'string') {
              proxyRes.headers['set-cookie'] = setCookie.replace(/;\s*Path=\/hyx/gi, '; Path=/');
            }
          });
        },
      },
    },
  },
  optimizeDeps: {
    include: ['react', 'react-dom', 'antd', '@ant-design/icons'],
  },
})
