import React, { useEffect } from 'react';
import { ConfigProvider, theme } from 'antd';
import { useTranslation } from 'react-i18next';
import Header from './components/Header';
import Hero from './components/Hero';
import Features from './components/Features';
import TechStack from './components/TechStack';
import Footer from './components/Footer';
import './i18n';

const App: React.FC = () => {
  const { t, i18n } = useTranslation();

  useEffect(() => {
    document.title = t('meta.title');
  }, [i18n.language, t]);

  return (
    <ConfigProvider
      theme={{
        algorithm: theme.darkAlgorithm,
        token: {
          colorPrimary: '#6366f1',
          fontFamily: 'Inter, sans-serif',
        },
      }}
    >
      <div className="app-container">
        <Header />
        <div style={{ paddingTop: '80px' }}>
          <Hero />
          <Features />
          <TechStack />
          <Footer />
        </div>
      </div>
    </ConfigProvider>
  );
};

export default App;
