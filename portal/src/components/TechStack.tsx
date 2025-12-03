import React from 'react';
import { Typography } from 'antd';
import { Trans } from 'react-i18next';

const { Title } = Typography;

const techs = [
    { name: 'Spring Boot 3', color: '#6db33f' },
    { name: 'React 18', color: '#61dafb' },
    { name: 'TypeScript', color: '#3178c6' },
    { name: 'Vite', color: '#646cff' },
    { name: 'Ant Design 5', color: '#1677ff' },
    { name: 'Docker', color: '#2496ed' },
    { name: 'MySQL', color: '#00758f' },
    { name: 'Redis', color: '#dc382d' }
];

const TechStack: React.FC = () => {
    return (
        <div style={{ padding: '5rem 0', background: '#0f172a' }}>
            <div className="container" style={{ textAlign: 'center' }}>
                <Title level={2} style={{ fontSize: '2.5rem', marginBottom: '3rem' }}>
                    <Trans i18nKey="techStack.title">
                        Built with Modern <span className="gradient-text">Tech Stack</span>
                    </Trans>
                </Title>

                <div style={{
                    display: 'flex',
                    flexWrap: 'wrap',
                    justifyContent: 'center',
                    gap: '1rem',
                    maxWidth: '800px',
                    margin: '0 auto'
                }}>
                    {techs.map((tech) => (
                        <div
                            key={tech.name}
                            className="glass-card"
                            style={{
                                padding: '1rem 2rem',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '0.5rem',
                                cursor: 'default',
                                transition: 'all 0.3s'
                            }}
                        >
                            <span style={{
                                width: '10px',
                                height: '10px',
                                borderRadius: '50%',
                                background: tech.color,
                                display: 'inline-block'
                            }} />
                            <span style={{ fontSize: '1.125rem', fontWeight: 500 }}>{tech.name}</span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default TechStack;
